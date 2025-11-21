package com.example.onharu.user.presentation.dto;

import com.example.onharu.user.application.dto.UserResult;
import com.example.onharu.user.domain.UserRole;

public record UserResponse(
        Long id,
        String name,
        String phone,
        UserRole role,
        int code
) {

    public static UserResponse from(UserResult result) {
        return new UserResponse(result.id(), result.name(), result.phone(), result.role(),
                result.code());
    }
}
