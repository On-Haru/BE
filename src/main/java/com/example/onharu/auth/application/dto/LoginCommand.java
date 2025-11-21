package com.example.onharu.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginCommand {

    private final String phone;
    private final String password;
}
