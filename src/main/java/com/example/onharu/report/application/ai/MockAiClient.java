package com.example.onharu.report.application.ai;

import com.example.onharu.report.application.AiClient;
import com.example.onharu.report.application.dto.AiRequest;
import com.example.onharu.report.application.dto.AiResponse;
import java.util.Collections;
import java.util.List;

public class MockAiClient implements AiClient {

    @Override
    public AiResponse generate(AiRequest request) {
        String summary = "[Mock AI] " + request.prompt();
        return new AiResponse(summary,
                "[Mock AI] 저녁 시간대를 챙겨주세요.",
                List.of("저녁 복약 주의"),
                Collections.emptyMap());
    }
}
