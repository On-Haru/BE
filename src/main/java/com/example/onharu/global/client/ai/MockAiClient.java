package com.example.onharu.global.client.ai;

public class MockAiClient implements AiClient {

    @Override
    public <TRequest, TResponse> TResponse execute(AiScenario<TRequest, TResponse> scenario,
            TRequest request) {
        return scenario.fallback(new IllegalStateException("Mock AI client"));
    }
}
