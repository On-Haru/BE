package com.example.onharu.auth.application.dto;

import com.example.onharu.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupCommand {

    private final String name;
    private final String phone;
    private final String year;
    private final String password;
    private final UserRole role;
}
