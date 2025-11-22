package com.example.onharu.auth.application.dto;

public record PasswordResetCommand(
        String phone,
        String newPassword
) {
}
