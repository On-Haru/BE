package com.example.onharu.push.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushSubscriptionJpaRepository extends JpaRepository<PushSubscription, Long>,
        PushSubscriptionRepository {

    @Override
    List<PushSubscription> findAllByUserId(Long userId);

    @Override
    Optional<PushSubscription> findByEndpoint(String endpoint);

}
