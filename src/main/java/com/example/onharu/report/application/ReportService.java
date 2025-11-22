package com.example.onharu.report.application;

import com.example.onharu.global.client.ai.AiClient;
import com.example.onharu.global.client.ai.dto.AiRequest;
import com.example.onharu.global.client.ai.dto.AiResponse;
import com.example.onharu.global.client.ai.dto.ReportPayload;
import com.example.onharu.report.application.ai.ReportAiScenario;
import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.report.domain.Report;
import com.example.onharu.report.domain.ReportRepository;
import com.example.onharu.user.domain.User;
import com.example.onharu.user.domain.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final StatisticsService statisticsService;
    private final PromptManager promptManager;
    private final AiClient aiClient;
    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ReportAiScenario reportAiScenario;

    public Optional<ReportPayload> getReport(Long seniorId, YearMonth month) {
        return reportRepository.findBySeniorIdAndReportDate(seniorId, month.atDay(1))
                .flatMap(report -> readPayload(report)
                        .map(payload -> payload.withReportId(report.getId())))
                .or(() -> generateAndSaveReport(seniorId, month));
    }

    public Optional<ReportPayload> generateAndSaveReport(Long seniorId, YearMonth month) {
        User senior = userRepository.findById(seniorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ReportPayload basePayload = statisticsService.buildPayload(senior, month);
        String prompt = promptManager.buildPrompt(basePayload, month);
        AiResponse aiResponse = aiClient.execute(reportAiScenario,
                new AiRequest(prompt, basePayload, month));

        String summary = aiResponse.summary() != null
                ? aiResponse.summary()
                : "복약 데이터를 바탕으로 생성된 요약입니다.";
        String suggestion = aiResponse.suggestion();
        List<String> riskTags = aiResponse.riskTags() == null ? List.of() : aiResponse.riskTags();
        var medicineComments = aiResponse.medicineComments() == null
                ? Collections.<String, String>emptyMap()
                : aiResponse.medicineComments();

        ReportPayload payload = basePayload.withAiInsights(summary, suggestion, riskTags,
                medicineComments);
        Report saved = saveReport(seniorId, month, payload);
        return Optional.of(payload.withReportId(saved.getId()));
    }

    private Optional<ReportPayload> readPayload(Report report) {
        try {
            return Optional.of(
                    objectMapper.readValue(report.getJsonPayload(), ReportPayload.class));
        } catch (JsonProcessingException e) {
            log.warn("리포트 페이로드 역직렬화 실패", e);
            return Optional.empty();
        }
    }

    private String writePayload(ReportPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("리포트 페이로드 직렬화에 실패했습니다.", e);
        }
    }

    @Transactional
    protected Report saveReport(Long seniorId, YearMonth month, ReportPayload payload) {
        String json = writePayload(payload);
        LocalDate reportDate = month.atDay(1);
        Report report = reportRepository.findBySeniorIdAndReportDate(seniorId, reportDate)
                .map(existing -> {
                    existing.updatePayload(json);
                    return existing;
                })
                .orElseGet(() -> Report.create(seniorId, reportDate,
                        payload.reportMeta().periodType(), json));
        return reportRepository.save(report);
    }
}
