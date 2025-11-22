package com.example.onharu.global.client.ai.dto;

public record AiRequest(
        String prompt,
        ReportPayload payload,
        java.time.YearMonth targetMonth
) {

}
