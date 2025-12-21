package com.example.onharu.push.application;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.push.domain.MobilePlatform;
import com.example.onharu.push.domain.MobilePushToken;
import com.example.onharu.push.domain.MobilePushTokenRepository;
import com.example.onharu.push.presentation.dto.MobilePushSubscribeRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class MobilePushTokenService {

    private final MobilePushTokenRepository mobilePushTokenRepository;

    @Transactional
    public void register(Long userId, MobilePushSubscribeRequest request) {
        String token = normalize(request.normalizedToken(), "token");
        String deviceId = normalize(request.normalizedDeviceId(), "deviceId");
        MobilePlatform platform = request.platform();

        try {
            MobilePushToken entity = mobilePushTokenRepository
                    .findByDeviceIdAndPlatform(deviceId, platform)
                    .map(existing -> {
                        existing.refresh(userId, token, request.appVersion(), request.osVersion(),
                                request.deviceModel());
                        return existing;
                    })
                    .orElseGet(() -> MobilePushToken.create(userId, token, platform, deviceId,
                            request.appVersion(), request.osVersion(), request.deviceModel()));

            mobilePushTokenRepository.save(entity);
            log.info("Registered mobile push token for user {} device {} platform {}", userId,
                    deviceId, platform);
        } catch (DataAccessException ex) {
            log.warn("Failed to save mobile push token for user {} device {}: {}", userId,
                    deviceId, ex.getMessage());
            throw new BusinessException(ErrorCode.MOBILE_PUSH_TOKEN_SAVE_FAILED);
        }
    }

    @Transactional
    public void unsubscribe(Long userId, String rawDeviceId) {
        String deviceId = normalize(rawDeviceId, "deviceId");
        List<MobilePushToken> tokens = mobilePushTokenRepository.findAllByDeviceId(deviceId);

        if (tokens.isEmpty()) {
            log.info("No mobile push token found for device {}. Skipping delete for user {}", deviceId,
                    userId);
            return;
        }

        boolean deleted = false;
        for (MobilePushToken token : tokens) {
            if (!token.belongsTo(userId)) {
                continue;
            }

            try {
                mobilePushTokenRepository.delete(token);
                deleted = true;
                log.info("Removed mobile push token for user {} device {} platform {}", userId,
                        deviceId, token.getPlatform());
            } catch (DataAccessException ex) {
                log.warn("Failed to delete mobile push token for user {} device {}: {}", userId,
                        deviceId, ex.getMessage());
                throw new BusinessException(ErrorCode.MOBILE_PUSH_TOKEN_DELETE_FAILED);
            }
        }

        if (!deleted) {
            throw new BusinessException(ErrorCode.MOBILE_PUSH_TOKEN_NOT_FOUND);
        }
    }

    private String normalize(String value, String field) {
        if (!StringUtils.hasText(value)) {
            log.warn("Invalid mobile push token field {}", field);
            throw new BusinessException(ErrorCode.MOBILE_PUSH_TOKEN_VALIDATION_FAILED);
        }
        return value.trim();
    }
}

