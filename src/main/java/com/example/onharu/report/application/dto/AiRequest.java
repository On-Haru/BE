package com.example.onharu.report.application.dto;

public record AiRequest(
        String prompt,
        ReportPayload payload,
        java.time.YearMonth targetMonth
) {
}
