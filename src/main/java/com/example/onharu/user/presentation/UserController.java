package com.example.onharu.user.presentation;

import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
import com.example.onharu.user.application.UserService;
import com.example.onharu.user.application.dto.UserResult;
import com.example.onharu.user.domain.UserRole;
import com.example.onharu.user.presentation.dto.UserResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        UserResult result = userService.getUser(userId);
        return ApiResponseFactory.success(UserResponse.from(result));
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsersByRole(@RequestParam UserRole role) {
        List<UserResponse> responses = userService.findByRole(role)
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
        return ApiResponseFactory.success(responses);
    }
}
