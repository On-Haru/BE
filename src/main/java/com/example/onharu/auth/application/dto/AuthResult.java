package com.example.onharu.auth.application.dto;


public record AuthResult(Long id, String token) {

    public static AuthResult of(Long id, String token) {
        return new AuthResult(id, token);
    }
}
