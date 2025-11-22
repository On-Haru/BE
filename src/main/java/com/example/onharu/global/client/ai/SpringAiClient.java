package com.example.onharu.global.client.ai;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringAiClient implements AiClient {

    private final ChatClient chatClient;

    @PostConstruct
    public void logHealthCheck() {
        try {
            chatClient.prompt()
                    .user("health-check")
                    .call();
            log.info("Spring AI ChatClient health check succeeded.");
        } catch (Exception ex) {
            log.warn("Spring AI ChatClient health check failed: {}", ex.getMessage());
        }
    }

    @Override
    public <TRequest, TResponse> TResponse execute(AiScenario<TRequest, TResponse> scenario,
            TRequest request) {
        AiPrompt prompt = scenario.buildPrompt(request);
        try {
            var spec = chatClient.prompt();
            if (prompt.system() != null && !prompt.system().isBlank()) {
                spec = spec.system(prompt.system());
            }
            String content = spec.user(prompt.user())
                    .call()
                    .content();
            if (content == null || content.isBlank()) {
                return scenario.fallback(new IllegalStateException("AI 응답이 비어 있습니다."));
            }
            return scenario.parseResponse(content);
        } catch (Exception e) {
            log.warn("AI 시나리오 {} 호출 실패", scenario.name(), e);
            return scenario.fallback(e);
        }
    }
}
