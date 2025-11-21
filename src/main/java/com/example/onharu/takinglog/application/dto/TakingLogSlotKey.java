package com.example.onharu.takinglog.application.dto;

import java.time.LocalDateTime;

public record TakingLogSlotKey(
        Long scheduleId,
        LocalDateTime scheduledDateTime
) {

    public static TakingLogSlotKey of(Long scheduleId, LocalDateTime scheduledDateTime) {
        return new TakingLogSlotKey(scheduleId, scheduledDateTime);
    }
}
