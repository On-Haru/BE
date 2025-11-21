package com.example.onharu.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "push.vapid")
public record PushProperties(String subject, String publicKey, String privateKey) {

}