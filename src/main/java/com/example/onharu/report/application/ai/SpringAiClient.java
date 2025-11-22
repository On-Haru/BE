package com.example.onharu.report.application.ai;

import com.example.onharu.report.application.AiClient;
import com.example.onharu.report.application.dto.AiRequest;
import com.example.onharu.report.application.dto.AiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringAiClient implements AiClient {

    private static final String SYSTEM_PROMPT = "당신은 복약 관리 전문가입니다. "
            + "반드시 JSON 객체만 응답하며 summary, suggestion, riskTags, medicineComments만 포함하세요.";

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void logHealthCheck() {
        try {
            chatClient.prompt()
                    .user("health-check")
                    .call();
            log.info("Spring AI ChatClient health check succeeded.");
        } catch (Exception ex) {
            log.warn("Spring AI ChatClient health check failed: {}", ex.getMessage());
        }
    }

    @Override
    public AiResponse generate(AiRequest request) {
        try {
            String payloadJson = objectMapper.writeValueAsString(request.payload());
            String userMessage = buildUserMessage(request.prompt(), payloadJson,
                    request.targetMonth().toString());

            String rawContent = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(userMessage)
                    .call()
                    .content();

            if (rawContent == null || rawContent.isBlank()) {
                return fallbackResponse();
            }

            AiStructuredResponse structured = parseResponse(rawContent);
            if (structured == null) {
                return fallbackResponse();
            }

            List<String> riskTags = structured.riskTags() == null
                    ? List.of()
                    : structured.riskTags();
            Map<String, String> medicineComments = structured.medicineComments() == null
                    ? Collections.emptyMap()
                    : structured.medicineComments();

            return new AiResponse(structured.summary(), structured.suggestion(), riskTags,
                    medicineComments);
        } catch (Exception e) {
            log.warn("AI 호출 실패", e);
            return fallbackResponse();
        }
    }

    private String buildUserMessage(String prompt, String payloadJson, String targetMonth) {
        return "목표 월: " + targetMonth + "\\n" +
                "사용자 프롬프트:\n" + prompt + "\\n\\n" +
                "ReportPayload JSON:\n" + payloadJson + "\\n" +
                "JSON 응답 예시는 {\"summary\":\"...\",\"suggestion\":\"...\"," +
                "\"riskTags\":[\"경고\"],\"medicineComments\":{\"아침 약\":\"코멘트\"}} 형식을 따르세요.";
    }

    private AiStructuredResponse parseResponse(String content) {
        try {
            String json = extractJson(content);
            return objectMapper.readValue(json, AiStructuredResponse.class);
        } catch (JsonProcessingException e) {
            log.warn("AI 응답 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    private String extractJson(String content) {
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start >= 0 && end >= start) {
            return content.substring(start, end + 1);
        }
        return content;
    }

    private AiResponse fallbackResponse() {
        return new AiResponse(
                "복약 데이터를 바탕으로 자체 생성된 기본 요약입니다. 꾸준히 복약을 이어가 주세요!",
                "저녁 시간대 복약 확인을 한 번 더 해주세요.",
                List.of("저녁 복약 주의"),
                Collections.emptyMap()
        );
    }

    private record AiStructuredResponse(
            String summary,
            String suggestion,
            List<String> riskTags,
            Map<String, String> medicineComments
    ) {
    }
}
