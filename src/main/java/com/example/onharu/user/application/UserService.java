package com.example.onharu.user.application;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.user.application.dto.UserResult;
import com.example.onharu.user.domain.User;
import com.example.onharu.user.domain.UserRepository;
import com.example.onharu.user.domain.UserRole;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResult getUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserResult::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public List<UserResult> findByRole(UserRole role) {
        return userRepository.findByRole(role)
                .stream()
                .map(UserResult::from)
                .collect(Collectors.toList());
    }

    private void isSenior(User user, UserRole role) {
        if (role == UserRole.SENIOR) {
            user.generateCode();
        }
    }
}
