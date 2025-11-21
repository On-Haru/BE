package com.example.onharu.medicineschedule.presentation.dto;

import com.example.onharu.medicineschedule.application.dto.MedicineScheduleCreateCommand;
import com.example.onharu.medicineschedule.domain.RepeatType;
import com.example.onharu.medicineschedule.domain.ScheduleType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record MedicineScheduleCreateRequest(
        @NotNull Long medicineId,
        @NotNull ScheduleType scheduleType,
        @NotNull RepeatType repeatType,
        Integer daysBitmask,
        @NotNull LocalTime notifyTime,
        @NotNull LocalDate startDate,
        LocalDate endDate
) {
    public MedicineScheduleCreateCommand toCommand() {
        return MedicineScheduleCreateCommand.of(medicineId, scheduleType, repeatType, daysBitmask, notifyTime, startDate, endDate);
    }
}
