package com.example.onharu.report.application.ai;

import com.example.onharu.global.client.ai.AiPrompt;
import com.example.onharu.global.client.ai.AiScenario;
import com.example.onharu.global.client.ai.dto.AiRequest;
import com.example.onharu.global.client.ai.dto.AiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportAiScenario implements AiScenario<AiRequest, AiResponse> {

    private static final String SYSTEM_PROMPT = "당신은 복약 관리 전문가입니다. "
            + "반드시 JSON 객체만 응답하며 summary, suggestion, riskTags, medicineComments만 포함하세요.";

    private final ObjectMapper objectMapper;

    @Override
    public String name() {
        return "report-ai";
    }

    @Override
    public AiPrompt buildPrompt(AiRequest request) {
        try {
            String payloadJson = objectMapper.writeValueAsString(request.payload());
            String userMessage = buildUserMessage(request.prompt(), payloadJson,
                    request.targetMonth().toString());
            return AiPrompt.of(SYSTEM_PROMPT, userMessage);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Report payload 직렬화 실패", e);
        }
    }

    @Override
    public AiResponse parseResponse(String rawContent) {
        try {
            String json = extractJson(rawContent);
            AiStructuredResponse structured = objectMapper.readValue(json,
                    AiStructuredResponse.class);
            List<String> riskTags = structured.riskTags() == null
                    ? List.of()
                    : structured.riskTags();
            Map<String, String> medicineComments = structured.medicineComments() == null
                    ? Collections.emptyMap()
                    : structured.medicineComments();
            return new AiResponse(structured.summary(), structured.suggestion(), riskTags,
                    medicineComments);
        } catch (Exception e) {
            log.warn("리포트 AI 응답 파싱 실패", e);
            return fallback(e);
        }
    }

    @Override
    public AiResponse fallback(Throwable cause) {
        return new AiResponse(
                "복약 데이터를 바탕으로 자체 생성된 기본 요약입니다. 꾸준히 복약을 이어가 주세요!",
                "저녁 시간대 복약 확인을 한 번 더 해주세요.",
                List.of("저녁 복약 주의"),
                Collections.emptyMap()
        );
    }

    private String buildUserMessage(String prompt, String payloadJson, String targetMonth) {
        return "목표 월: " + targetMonth + "\\n" +
                "사용자 프롬프트:\n" + prompt + "\\n\\n" +
                "ReportPayload JSON:\n" + payloadJson + "\\n" +
                "JSON 응답 예시는 {\"summary\":\"...\",\"suggestion\":\"...\"," +
                "\"riskTags\":[\"경고\"],\"medicineComments\":{\"아침 약\":\"코멘트\"}} 형식을 따르세요.";
    }

    private String extractJson(String content) {
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start >= 0 && end >= start) {
            return content.substring(start, end + 1);
        }
        return content;
    }

    private record AiStructuredResponse(
            String summary,
            String suggestion,
            List<String> riskTags,
            Map<String, String> medicineComments
    ) {
    }
}
