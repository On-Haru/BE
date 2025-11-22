package com.example.onharu.report.application.config;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ai.client")
public class AiClientProperties {

    private boolean enabled = false;
    private String baseUrl = "";
    private String apiKey = "";
    private Duration timeout = Duration.ofSeconds(10);
}
