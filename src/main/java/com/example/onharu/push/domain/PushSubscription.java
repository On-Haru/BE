package com.example.onharu.push.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "push_subscriptions")
public class PushSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 512, unique = true)
    private String endpoint;

    @Column(name = "expiration_time")
    private Long expirationTime;

    @Column(nullable = false, length = 88)
    private String p256dh;

    @Column(nullable = false)
    private String auth;

    private PushSubscription(Long userId, String endpoint, Long expirationTime, String p256dh,
            String auth) {
        this.userId = userId;
        this.endpoint = endpoint;
        this.expirationTime = expirationTime;
        this.p256dh = p256dh;
        this.auth = auth;
    }

    public static PushSubscription create(Long userId, String endpoint, Long expirationTime,
            String p256dh
            , String auth) {
        return new PushSubscription(userId, endpoint, expirationTime, p256dh, auth);
    }

    public void refresh(Long userId, Long expirationTime, String p256dh, String auth) {
        this.userId = userId;
        this.expirationTime = expirationTime;
        this.p256dh = p256dh;
        this.auth = auth;
    }
}
