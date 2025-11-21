package com.example.onharu.user.application.dto;

import com.example.onharu.user.domain.UserRole;

public record UserCreateCommand(
        String name,
        String phone,
        UserRole role
) {
    public static UserCreateCommand of(String name, String phone, UserRole role) {
        return new UserCreateCommand(name, phone, role);
    }
}
