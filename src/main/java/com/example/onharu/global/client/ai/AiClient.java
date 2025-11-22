package com.example.onharu.global.client.ai;

public interface AiClient {

    <TRequest, TResponse> TResponse execute(AiScenario<TRequest, TResponse> scenario,
            TRequest request);
}
