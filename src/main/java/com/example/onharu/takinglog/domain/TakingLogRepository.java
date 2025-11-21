package com.example.onharu.takinglog.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TakingLogRepository {

    TakingLog save(TakingLog takingLog);

    Optional<TakingLog> findById(Long id);

    List<TakingLog> findByScheduleId(Long scheduleId);

    Optional<TakingLog> findByScheduleIdAndScheduledDateTime(Long scheduleId,
            java.time.LocalDateTime scheduledDateTime);

    List<TakingLog> findBySeniorIdAndScheduledDateBetween(Long seniorId,
            LocalDateTime startInclusive,
            LocalDateTime endExclusive);
}
