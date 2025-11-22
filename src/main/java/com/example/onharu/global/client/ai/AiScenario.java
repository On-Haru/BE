package com.example.onharu.global.client.ai;

public interface AiScenario<TRequest, TResponse> {

    String name();

    AiPrompt buildPrompt(TRequest request);

    TResponse parseResponse(String rawContent) throws Exception;

    TResponse fallback(Throwable cause);
}
