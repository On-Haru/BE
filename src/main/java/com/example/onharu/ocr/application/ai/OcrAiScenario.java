package com.example.onharu.ocr.application.ai;

import com.example.onharu.global.client.ai.AiPrompt;
import com.example.onharu.global.client.ai.AiScenario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcrAiScenario implements AiScenario<OcrAiScenario.Request, OcrAiScenario.Response> {

    private static final String SYSTEM_PROMPT = "당신은 처방전 OCR 후처리 전문가입니다. "
            + "입력된 문자열을 분석해 JSON 객체만 반환하세요.";

    private final ObjectMapper objectMapper;

    @Override
    public String name() {
        return "ocr-structuring";
    }

    @Override
    public AiPrompt buildPrompt(Request request) {
        StringBuilder user = new StringBuilder();
        user.append("다음 텍스트를 약 이름, 복용량, 기간, 메모 등으로 구조화된 JSON으로 변환하세요.\n")
                .append("출력 예시는 {\"medicines\":[{\"name\":\"고혈압약\",\"dosage\":1}]}입니다.\n")
                .append("텍스트:\n")
                .append(request.ocrText());
        return AiPrompt.of(SYSTEM_PROMPT, user.toString());
    }

    @Override
    public Response parseResponse(String rawContent) throws Exception {
        String json = extractJson(rawContent);
        Map<String, Object> payload = objectMapper.readValue(json, new TypeReference<>() {
        });
        return new Response(payload);
    }

    @Override
    public Response fallback(Throwable cause) {
        log.warn("OCR AI 시나리오 실패", cause);
        return new Response(Collections.emptyMap());
    }

    private String extractJson(String content) {
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start >= 0 && end >= start) {
            return content.substring(start, end + 1);
        }
        return content;
    }

    public record Request(String ocrText) {
    }

    public record Response(Map<String, Object> structured) {
    }
}
