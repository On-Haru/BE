package com.example.onharu.push.presentation;

import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.global.jwt.LoginUser;
import com.example.onharu.push.application.MobilePushTokenService;
import com.example.onharu.push.presentation.dto.MobilePushSubscribeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push/mobile")
public class MobilePushController {

    private final MobilePushTokenService mobilePushTokenService;

    @PostMapping("/subscribe")
    public ApiResponse<Boolean> subscribe(@LoginUser Long userId,
            @Valid @RequestBody MobilePushSubscribeRequest request) {
        ensureAuthenticated(userId);
        mobilePushTokenService.register(userId, request);
        return ApiResponseFactory.success(true);
    }

    @DeleteMapping("/subscribe/{deviceId}")
    public ApiResponse<Boolean> unsubscribe(@LoginUser Long userId,
            @PathVariable String deviceId) {
        ensureAuthenticated(userId);
        mobilePushTokenService.unsubscribe(userId, deviceId);
        return ApiResponseFactory.success(true);
    }

    private void ensureAuthenticated(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }
    }
}

