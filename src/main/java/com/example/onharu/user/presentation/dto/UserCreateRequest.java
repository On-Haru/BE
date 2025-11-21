package com.example.onharu.user.presentation.dto;

import com.example.onharu.user.application.dto.UserCreateCommand;
import com.example.onharu.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(
        @NotBlank String name,
        @NotBlank String phone,
        @NotNull UserRole role
) {

    public UserCreateCommand toCommand() {
        return UserCreateCommand.of(name, phone, role);
    }
}
