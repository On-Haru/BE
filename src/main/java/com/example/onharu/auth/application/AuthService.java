package com.example.onharu.auth.application;

import com.example.onharu.auth.application.dto.AuthResult;
import com.example.onharu.auth.application.dto.LoginCommand;
import com.example.onharu.auth.application.dto.PasswordResetCommand;
import com.example.onharu.auth.application.dto.SignupCommand;
import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.global.jwt.JwtProvider;
import com.example.onharu.global.jwt.LogoutTokenStore;
import com.example.onharu.user.domain.User;
import com.example.onharu.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final LogoutTokenStore logoutTokenStore;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResult signup(SignupCommand command) {
        if (userRepository.existsByPhone(command.getPhone())) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_REGISTERED);
        }

        String encodedPassword = passwordEncoder.encode(command.getPassword());

        User user = User.create(
                command.getName(),
                command.getPhone(),
                command.getRole(),
                command.getYear(),
                encodedPassword
        );

        user.generateCode();
        User savedUser = userRepository.save(user);

        return AuthResult.of(savedUser.getId(), null);
    }

    @Transactional
    public AuthResult login(LoginCommand command) {
        User user = userRepository.findByPhone(command.getPhone())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtProvider.generateToken(
                user.getId(),
                user.getRole(),
                user.getPhone()
        );

        return AuthResult.of(user.getId(), token);
    }

    @Transactional
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        var expiration = jwtProvider.getExpiration(token);
        logoutTokenStore.revoke(token, expiration);
    }

    @Transactional
    public void resetPassword(Long requesterId, PasswordResetCommand command) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //TODO: 권한 검증 로직 추가 (예: 관리자만 가능)

        User target = userRepository.findByPhone(command.phone())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String encoded = passwordEncoder.encode(command.newPassword());
        target.updatePassword(encoded);
    }
}
