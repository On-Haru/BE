package com.example.onharu.user.application.dto;

import com.example.onharu.user.domain.User;
import com.example.onharu.user.domain.UserRole;

public record UserResult(
        Long id,
        String name,
        String phone,
        UserRole role,
        int code
) {

    public static UserResult from(User user) {
        return new UserResult(user.getId(), user.getName(), user.getPhone(), user.getRole(),
                user.getCode());
    }
}
