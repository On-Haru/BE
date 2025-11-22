package com.example.onharu.report.application.dto;

import java.util.List;
import java.util.Map;

public record AiResponse(
        String summary,
        String suggestion,
        List<String> riskTags,
        Map<String, String> medicineComments
) {
}
