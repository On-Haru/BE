package com.example.onharu.takinglog.application.dto;


import java.time.LocalDateTime;

public record TakenLogCommand(
        TakingLogSlotKey slotKey,
        boolean taken
) {

    public static TakenLogCommand of(Long scheduleId, LocalDateTime scheduledDateTime,
            boolean taken) {
        return new TakenLogCommand(TakingLogSlotKey.of(scheduleId, scheduledDateTime), taken);
    }
}
