package com.example.onharu.push.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "mobile_push_tokens",
        uniqueConstraints = @UniqueConstraint(name = "uk_mobile_push_tokens_device_platform",
                columnNames = {"device_id", "platform"}))
public class MobilePushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 512)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MobilePlatform platform;

    @Column(name = "device_id", nullable = false, length = 128)
    private String deviceId;

    @Column(name = "app_version", length = 64)
    private String appVersion;

    @Column(name = "os_version", length = 32)
    private String osVersion;

    @Column(name = "device_model", length = 128)
    private String deviceModel;

    private MobilePushToken(Long userId, String token, MobilePlatform platform, String deviceId,
            String appVersion, String osVersion, String deviceModel) {
        this.userId = userId;
        this.token = token;
        this.platform = platform;
        this.deviceId = deviceId;
        this.appVersion = appVersion;
        this.osVersion = osVersion;
        this.deviceModel = deviceModel;
    }

    public static MobilePushToken create(Long userId, String token, MobilePlatform platform,
            String deviceId, String appVersion, String osVersion, String deviceModel) {
        return new MobilePushToken(userId, token, platform, deviceId, appVersion, osVersion,
                deviceModel);
    }

    public void refresh(Long userId, String token, String appVersion, String osVersion,
            String deviceModel) {
        this.userId = userId;
        this.token = token;
        this.appVersion = appVersion;
        this.osVersion = osVersion;
        this.deviceModel = deviceModel;
    }

    public boolean belongsTo(Long userId) {
        return Objects.equals(this.userId, userId);
    }
}

