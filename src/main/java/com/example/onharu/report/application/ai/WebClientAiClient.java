package com.example.onharu.report.application.ai;

import com.example.onharu.report.application.AiClient;
import com.example.onharu.report.application.dto.AiRequest;
import com.example.onharu.report.application.dto.AiResponse;
import com.example.onharu.report.application.config.AiClientProperties;
import com.example.onharu.report.application.dto.ReportPayload;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Slf4j
public class WebClientAiClient implements AiClient {

    private final WebClient webClient;
    private final AiClientProperties properties;

    @Override
    public AiResponse generate(AiRequest request) {
        try {
            AiHttpResponse response = webClient.post()
                    .uri(properties.getBaseUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new AiHttpRequest(request.prompt(), request.payload(),
                            request.targetMonth().toString()))
                    .retrieve()
                    .bodyToMono(AiHttpResponse.class)
                    .block(properties.getTimeout());
            if (response == null) {
                return fallbackResponse();
            }
            return new AiResponse(response.summary(), response.suggestion(), response.riskTags(),
                    response.medicineComments());
        } catch (Exception e) {
            log.warn("AI 호출 실패: {}", e.getMessage());
            return fallbackResponse();
        }
    }

    private AiResponse fallbackResponse() {
        return new AiResponse(
                "복약 데이터를 바탕으로 자체 생성된 기본 요약입니다. 꾸준히 복약을 이어가 주세요!",
                "저녁 시간대 복약 확인을 한 번 더 해주세요.",
                List.of("저녁 복약 주의"),
                Collections.emptyMap()
        );
    }

    private record AiHttpRequest(String prompt, ReportPayload payload, String targetMonth) {
    }

    private record AiHttpResponse(
            String summary,
            String suggestion,
            List<String> riskTags,
            java.util.Map<String, String> medicineComments
    ) {
    }
}
