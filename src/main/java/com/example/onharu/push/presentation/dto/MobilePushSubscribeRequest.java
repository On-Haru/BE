package com.example.onharu.push.presentation.dto;

import com.example.onharu.push.domain.MobilePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MobilePushSubscribeRequest(
        @NotBlank(message = "토큰은 필수입니다.") String token,
        @NotNull(message = "플랫폼은 필수입니다.") MobilePlatform platform,
        @NotBlank(message = "디바이스 ID는 필수입니다.") String deviceId,
        String appVersion,
        String osVersion,
        String deviceModel
) {

    public String normalizedDeviceId() {
        return deviceId == null ? null : deviceId.trim();
    }

    public String normalizedToken() {
        return token == null ? null : token.trim();
    }
}

