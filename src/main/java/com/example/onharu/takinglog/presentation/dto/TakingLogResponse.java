package com.example.onharu.takinglog.presentation.dto;

import com.example.onharu.medicineschedule.domain.ScheduleType;
import com.example.onharu.takinglog.application.dto.TakingLogSlotDto;
import java.time.LocalDateTime;

public record TakingLogResponse(
        Long id,
        Long scheduleId,
        String medicineName,
        ScheduleType scheduleType,
        LocalDateTime scheduledDateTime,
        LocalDateTime takenDateTime,
        boolean taken,
        Integer delayMinutes
) {
    public static TakingLogResponse from(TakingLogSlotDto result) {
        return new TakingLogResponse(
                result.id(),
                result.scheduleId(),
                result.medicineName(),
                result.scheduleType(),
                result.scheduledDateTime(),
                result.takenDateTime(),
                result.taken(),
                result.delayMinutes()
        );
    }
}
