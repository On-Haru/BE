package com.example.onharu.push.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum MobilePlatform {
    ANDROID,
    IOS;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MobilePlatform from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("플랫폼은 필수입니다.");
        }
        return Arrays.stream(values())
                .filter(platform -> platform.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("지원하지 않는 플랫폼입니다: " + value));
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}

