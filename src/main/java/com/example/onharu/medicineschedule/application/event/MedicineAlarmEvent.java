package com.example.onharu.medicineschedule.application.event;

import java.time.LocalDateTime;

public record MedicineAlarmEvent(
        Long scheduleId,
        Long userId,
        LocalDateTime scheduledDateTime,
        LocalDateTime triggeredAt,
        String channel,
        boolean success,
        String failureReason
) {
    public static MedicineAlarmEvent of(Long scheduleId, Long userId,
            LocalDateTime scheduledDateTime, LocalDateTime triggeredAt,
            String channel, boolean success, String failureReason) {
        return new MedicineAlarmEvent(scheduleId, userId, scheduledDateTime, triggeredAt,
                channel, success, failureReason);
    }
}
