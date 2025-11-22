package com.example.onharu.report.application.config;

import com.example.onharu.report.application.AiClient;
import com.example.onharu.report.application.ai.MockAiClient;
import com.example.onharu.report.application.ai.WebClientAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class AiClientConfig {

    private final AiClientProperties properties;

    @Bean
    @ConditionalOnProperty(name = "ai.client.enabled", havingValue = "true")
    public AiClient webClientAiClient(WebClient.Builder builder) {
        WebClient client = builder
                .baseUrl(properties.getBaseUrl())
                .build();
        return new WebClientAiClient(client, properties);
    }

    @Bean
    @ConditionalOnMissingBean(AiClient.class)
    public AiClient mockAiClient() {
        return new MockAiClient();
    }
}
