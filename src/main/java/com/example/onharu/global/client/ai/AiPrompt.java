package com.example.onharu.global.client.ai;

public record AiPrompt(
        String system,
        String user
) {
    public static AiPrompt of(String system, String user) {
        return new AiPrompt(system, user);
    }
}
