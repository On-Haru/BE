package com.example.onharu.auth.presentation.dto;

import com.example.onharu.auth.application.dto.AuthResult;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(Long userId, String token) {

    public static AuthResponse from(AuthResult result) {
        return new AuthResponse(result.id(), result.token());
    }
}