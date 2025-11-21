package com.example.onharu.alarm.application.dto;

import java.time.LocalDateTime;

public record AlarmHistoryCreateCommand(
        Long scheduleId,
        String channel,
        LocalDateTime sentAt,
        boolean success,
        String failureReason
) {
    public static AlarmHistoryCreateCommand of(Long scheduleId, String channel, LocalDateTime sentAt,
                                               boolean success, String failureReason) {
        return new AlarmHistoryCreateCommand(scheduleId, channel, sentAt, success, failureReason);
    }
}
