package com.example.onharu.auth.presentation.dto;

import com.example.onharu.auth.application.dto.LoginCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @NotNull(message = "비밀번호는 필수입니다.")
    private String password;

    public LoginCommand toCommand() {
        return new LoginCommand(phone, password);
    }
}
