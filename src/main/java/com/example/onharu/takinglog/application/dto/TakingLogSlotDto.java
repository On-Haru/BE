package com.example.onharu.takinglog.application.dto;

import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.ScheduleType;
import com.example.onharu.takinglog.domain.TakingLog;
import java.time.LocalDateTime;

public record TakingLogSlotDto(
        Long id,
        Long scheduleId,
        String medicineName,
        ScheduleType scheduleType,
        LocalDateTime scheduledDateTime,
        LocalDateTime takenDateTime,
        boolean taken,
        Integer delayMinutes
) {

    public static TakingLogSlotDto from(TakingLog log) {
        MedicineSchedule schedule = log.getSchedule();
        return new TakingLogSlotDto(
                log.getId(),
                schedule.getId(),
                schedule.getMedicine().getName(),
                schedule.getScheduleType(),
                log.getScheduledDateTime(),
                log.getTakenDateTime(),
                log.isTaken(),
                log.getDelayMinutes()
        );
    }
}
