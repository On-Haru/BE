package com.example.onharu.push.presentation;

import com.example.onharu.push.application.PushSubscriptionService;
import com.example.onharu.push.presentation.dto.NotifyRequest;
import com.example.onharu.push.presentation.dto.SubscriptionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push")
//TODO: 인증 적용
public class PushController {

    private final PushSubscriptionService pushSubscriptionService;

    @PostMapping("/subscribe/{userId}")
    public ResponseEntity<Void> subscribe(@RequestBody SubscriptionRequest request,
            @PathVariable Long userId) {
        pushSubscriptionService.subscribe(request, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notify/{userId}")
    public ResponseEntity<Void> notify(@RequestBody NotifyRequest request,
            @PathVariable Long userId) {
        pushSubscriptionService.sendNotification(request, userId);
        return ResponseEntity.ok().build();
    }
}
