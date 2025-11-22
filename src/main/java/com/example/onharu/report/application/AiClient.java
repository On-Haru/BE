package com.example.onharu.report.application;

import com.example.onharu.report.application.dto.AiRequest;
import com.example.onharu.report.application.dto.AiResponse;

public interface AiClient {

    AiResponse generate(AiRequest request);
}
