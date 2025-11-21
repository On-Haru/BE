package com.example.onharu.medicineschedule.application.dto;

import com.example.onharu.medicineschedule.domain.RepeatType;
import com.example.onharu.medicineschedule.domain.ScheduleType;
import java.time.LocalDate;
import java.time.LocalTime;

public record MedicineScheduleCreateCommand(
        Long medicineId,
        ScheduleType scheduleType,
        RepeatType repeatType,
        Integer daysBitmask,
        LocalTime notifyTime,
        LocalDate startDate,
        LocalDate endDate
) {
    public static MedicineScheduleCreateCommand of(Long medicineId, ScheduleType scheduleType, RepeatType repeatType,
                                                   Integer daysBitmask, LocalTime notifyTime, LocalDate startDate,
                                                   LocalDate endDate) {
        return new MedicineScheduleCreateCommand(medicineId, scheduleType, repeatType, daysBitmask, notifyTime, startDate, endDate);
    }
}
