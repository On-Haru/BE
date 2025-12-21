package com.example.onharu.push.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobilePushTokenRepository extends JpaRepository<MobilePushToken, Long> {

    Optional<MobilePushToken> findByDeviceIdAndPlatform(String deviceId, MobilePlatform platform);

    List<MobilePushToken> findAllByDeviceId(String deviceId);
}

