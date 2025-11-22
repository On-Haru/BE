package com.example.onharu.ocr.application.ai;

import com.example.onharu.global.client.ai.AiPrompt;
import com.example.onharu.global.client.ai.AiScenario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcrAiScenario implements AiScenario<OcrAiScenario.Request, OcrAiScenario.Response> {

    private static final String SYSTEM_PROMPT = "당신은 처방전 OCR 후처리 전문가입니다. "
            + "반드시 지정된 JSON 스키마에 딱 맞는 결과만 반환하세요.";

    private final ObjectMapper objectMapper;

    @Override
    public String name() {
        return "ocr-structuring";
    }

    @Override
    public AiPrompt buildPrompt(Request request) {
        StringBuilder user = new StringBuilder();
        user.append("아래는 OCR JSON 원문입니다. 해당 데이터를 좌표 기반 규칙에 따라 파싱하여 처방전 정보를 추출하세요.\n")
                .append("규칙:\n")
                .append("1) 같은 행 여부: centerY 차이 30px 이하\n")
                .append("2) 열 구간: 약명 X=800~1200, 수량관련 X=1200~1500, 주의/설명 X>1500\n")
                .append("3) 주의 문구 여러 줄은 하나의 문자열로 합치기\n")
                .append("4) 숫자 누락 시 패턴에서 추론 (예: '1 3 3')\n")
                .append("5) 잘못 인식된 약명은 실제 의약품명으로 자동 정정\n")
                .append("데이터 추출 규칙:\n")
                .append("- issuedDate는 YYYY-MM-DD 형식\n")
                .append("- dosage, totalCount, durationDays는 정수\n")
                .append("- memo는 경고/보관 지침\n")
                .append("- aiDescription은 약 목적을 한 문장으로\n")
                .append("- schedules는 반드시 비어 있지 않아야 하며 MORNING/LUNCH/EVENING/NIGHT 중 택1\n")
                .append("- 복용 횟수 = 3이면 기본 시간 09:00,13:00,19:00 사용\n")
                .append("- 값이 불명확하면 null을 채우되 필드는 삭제하지 말 것\n")
                .append("출력 JSON 스키마:\n")
                .append("{\n  \"seniorId\": null,\n  \"hospitalName\": \"...\",\n  \"doctorName\": \"...\",\n  \"issuedDate\": \"YYYY-MM-DD\",\n  \"note\": null,\n  \"medicines\": [\n    {\n      \"name\": \"...\",\n      \"dosage\": 0,\n      \"totalCount\": 0,\n      \"durationDays\": 0,\n      \"memo\": \"...\",\n      \"aiDescription\": \"...\",\n      \"schedules\": [\n        { \"notifyTime\": \"HH:mm\", \"timeTag\": \"MORNING/LUNCH/EVENING/NIGHT\" }\n      ]\n    }\n  ]\n}\n")
                .append("OCR 원문:\n")
                .append(request.ocrText());
        return AiPrompt.of(SYSTEM_PROMPT, user.toString());
    }

    @Override
    public Response parseResponse(String rawContent) throws Exception {
        String json = extractJson(rawContent);
        return objectMapper.readValue(json, Response.class);
    }

    @Override
    public Response fallback(Throwable cause) {
        log.warn("OCR AI 시나리오 실패", cause);
        return new Response(null, null, null, null, null, Collections.emptyList());
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            Long seniorId,
            String hospitalName,
            String doctorName,
            String issuedDate,
            String note,
            List<Medicine> medicines
    ) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Medicine(
            Long id,
            String name,
            Integer dosage,
            Integer totalCount,
            Integer durationDays,
            String memo,
            String aiDescription,
            List<Schedule> schedules
    ) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Schedule(
            Long id,
            String notifyTime,
            String timeTag
    ) {

    }
}
