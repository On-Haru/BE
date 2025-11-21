package com.example.onharu.takinglog.presentation.dto;

import com.example.onharu.takinglog.application.dto.TakingLogResult;
import java.time.LocalDateTime;

public record TakingLogResponse(
        Long id,
        Long scheduleId,
        LocalDateTime scheduledDateTime,
        LocalDateTime takenDateTime,
        boolean taken,
        Integer delayMinutes
) {
    public static TakingLogResponse from(TakingLogResult result) {
        return new TakingLogResponse(
                result.id(),
                result.scheduleId(),
                result.scheduledDateTime(),
                result.takenDateTime(),
                result.taken(),
                result.delayMinutes()
        );
    }
}
