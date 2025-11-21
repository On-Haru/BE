package com.example.onharu.auth.application;

import com.example.onharu.auth.application.dto.AuthResult;
import com.example.onharu.auth.application.dto.LoginCommand;
import com.example.onharu.auth.application.dto.SignupCommand;
import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.global.jwt.JwtProvider;
import com.example.onharu.user.domain.User;
import com.example.onharu.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResult signup(SignupCommand command) {
        if (userRepository.existsByPhone(command.getPhone())) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_REGISTERED);
        }

        User user = User.create(
                command.getName(),
                command.getPhone(),
                command.getRole(),
                command.getYear(),
                command.getPassword()
        );

        user.generateCode();
        User savedUser = userRepository.save(user);

        return AuthResult.of(savedUser.getId(), null);
    }

    @Transactional
    public AuthResult login(LoginCommand command) {
        //TODO: 비밀번호 인증 로직
        User user = userRepository.findByPhone(command.getPhone())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String token = jwtProvider.generateToken(
                user.getId(),
                user.getRole(),
                user.getPhone()
        );

        return AuthResult.of(user.getId(), token);
    }
}
