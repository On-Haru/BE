package com.example.onharu.auth.presentation.dto;

import com.example.onharu.auth.application.dto.PasswordResetCommand;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "전화번호를 입력해주세요.")
        String phone,
        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        String newPassword
) {

    public PasswordResetCommand toCommand() {
        return new PasswordResetCommand(phone, newPassword);
    }
}
