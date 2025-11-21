package com.example.onharu.alarm.application.dto;

import com.example.onharu.alarm.domain.AlarmHistory;
import java.time.LocalDateTime;

public record AlarmHistoryResult(
        Long id,
        Long scheduleId,
        String channel,
        LocalDateTime sentAt,
        boolean success,
        String failureReason
) {
    public static AlarmHistoryResult from(AlarmHistory history) {
        return new AlarmHistoryResult(
                history.getId(),
                history.getSchedule().getId(),
                history.getChannel(),
                history.getSentAt(),
                history.isSuccess(),
                history.getFailureReason()
        );
    }
}
