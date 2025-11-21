package com.example.onharu.push.application;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.global.properties.PushProperties;
import com.example.onharu.global.util.GsonUtil;
import com.example.onharu.push.domain.PushSubscription;
import com.example.onharu.push.domain.PushSubscriptionRepository;
import com.example.onharu.push.presentation.dto.NotifyRequest.NotifyData;
import com.example.onharu.push.presentation.dto.SubscriptionRequest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushSubscriptionService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final PushProperties properties;


    @Transactional
    public void subscribe(SubscriptionRequest request, Long userId) {
        Optional<PushSubscription> existing = pushSubscriptionRepository.findByEndpoint(
                request.endpoint());

        if (existing.isPresent()) {
            existing.get()
                    .refresh(userId, request.expirationTime(), request.keys().p256dh(),
                            request.keys().auth());
            return;
        }

        PushSubscription subscription = PushSubscription.create(
                userId,
                request.endpoint(),
                request.expirationTime(),
                request.keys().p256dh(),
                request.keys().auth());

        pushSubscriptionRepository.save(subscription);
    }

    public boolean sendNotification(NotifyData request, Long userId) {
        try {
            PushService webPush;
            try {
                webPush = buildPushService();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException |
                     NoSuchProviderException e) {
                throw new BusinessException(ErrorCode.PUSH_NOTIFICATION_FAILED);
            }

            byte[] payload = GsonUtil.toJson(
                    request.scheduleId(),
                    request.title(),
                    request.body(),
                    request.scheduledDateTime());
            boolean sent = false;

            for (PushSubscription entity : pushSubscriptionRepository.findAllByUserId(userId)) {
                try {
                    Notification notification = new Notification(
                            entity.getEndpoint(),
                            entity.getP256dh(),
                            entity.getAuth(),
                            payload
                    );
                    sendToSubscriber(webPush, entity, notification);
                    sent = true;
                } catch (Exception e) {
                    log.warn("Failed to create or send notification for endpoint {}: {}",
                            entity.getEndpoint(), e.getMessage());
                    removeInvalidSubscription(entity);
                }
            }
            return sent;
        } catch (RuntimeException e) {
            log.warn("Failed to send push notification for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    private void sendToSubscriber(PushService webPush, PushSubscription entity,
            Notification notification) {
        try {
            webPush.send(notification);
        } catch (Exception e) {
            removeInvalidSubscription(entity);
        }
    }

    private void removeInvalidSubscription(PushSubscription entity) {
        try {
            pushSubscriptionRepository.delete(entity);
        } catch (Exception ignore) {
            // 삭제 실패시 무시(정리 작업에 실패한 경우)
        }
    }

    private PushService buildPushService()
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        PushService pushService = new PushService();
        pushService.setSubject(properties.subject());
        pushService.setPublicKey(properties.publicKey());
        pushService.setPrivateKey(properties.privateKey());
        return pushService;
    }

}
