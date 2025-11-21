package com.example.onharu.auth.presentation;

import com.example.onharu.auth.application.AuthService;
import com.example.onharu.auth.application.dto.AuthResult;
import com.example.onharu.auth.presentation.dto.AuthResponse;
import com.example.onharu.auth.presentation.dto.LoginRequest;
import com.example.onharu.auth.presentation.dto.SignupRequest;
import com.example.onharu.global.api.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        AuthResult result = authService.signup(request.toCommand());
        return ResponseEntity.ok(ApiResponseFactory.success(AuthResponse.from(result)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.login(request.toCommand());
        return ResponseEntity.ok(ApiResponseFactory.success(AuthResponse.from(result)));
    }
}
