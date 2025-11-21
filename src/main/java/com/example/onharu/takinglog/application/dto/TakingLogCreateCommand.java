package com.example.onharu.takinglog.application.dto;

import java.time.LocalDateTime;

public record TakingLogCreateCommand(
        Long scheduleId,
        LocalDateTime scheduledDateTime,
        LocalDateTime takenDateTime,
        boolean taken,
        Integer delayMinutes
) {

    public static TakingLogCreateCommand of(Long scheduleId, LocalDateTime scheduledDateTime,
            LocalDateTime takenDateTime, boolean taken, Integer delayMinutes) {
        return new TakingLogCreateCommand(scheduleId, scheduledDateTime, takenDateTime, taken,
                delayMinutes);
    }
}
