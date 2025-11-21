package com.example.onharu.takinglog.application.dto;

import com.example.onharu.takinglog.domain.TakingLog;
import java.time.LocalDateTime;

public record TakingLogResult(
        Long id,
        Long scheduleId,
        LocalDateTime scheduledDateTime,
        LocalDateTime takenDateTime,
        boolean taken,
        Integer delayMinutes
) {
    public static TakingLogResult from(TakingLog log) {
        return new TakingLogResult(
                log.getId(),
                log.getSchedule().getId(),
                log.getScheduledDateTime(),
                log.getTakenDateTime(),
                log.isTaken(),
                log.getDelayMinutes()
        );
    }
}
