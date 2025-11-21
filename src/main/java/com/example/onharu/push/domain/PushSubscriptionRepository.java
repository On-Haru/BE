package com.example.onharu.push.domain;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository {

    PushSubscription save(PushSubscription subscription);

    List<PushSubscription> findAllByUserId(Long userId);

    Optional<PushSubscription> findByEndpoint(String endpoint);

    void delete(PushSubscription subscription);
}
