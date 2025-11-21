package com.example.onharu.alarm.presentation.dto;

import com.example.onharu.alarm.application.dto.AlarmHistoryResult;
import java.time.LocalDateTime;

public record AlarmHistoryResponse(
        Long id,
        Long scheduleId,
        String channel,
        LocalDateTime sentAt,
        boolean success,
        String failureReason
) {
    public static AlarmHistoryResponse from(AlarmHistoryResult result) {
        return new AlarmHistoryResponse(
                result.id(),
                result.scheduleId(),
                result.channel(),
                result.sentAt(),
                result.success(),
                result.failureReason()
        );
    }
}
