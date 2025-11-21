package com.example.onharu.global.jwt;

import com.example.onharu.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtPayload {
    private final Long userId;
    private final UserRole role;
    private final String phone;
}
