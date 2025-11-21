package com.example.onharu.push.presentation.dto;

public record SubscriptionRequest(
        String endpoint,
        Long expirationTime,
        Keys keys
) {

    public record Keys(String p256dh, String auth) {

    }
}

