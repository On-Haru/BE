package com.example.onharu.auth.presentation.dto;

import com.example.onharu.auth.application.dto.SignupCommand;
import com.example.onharu.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @NotBlank(message = "년도는 필수입니다.")
    private String year;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotNull(message = "역할은 필수입니다.")
    private UserRole role;

    public SignupCommand toCommand() {
        return new SignupCommand(name, phone, year, password, role);
    }
}
