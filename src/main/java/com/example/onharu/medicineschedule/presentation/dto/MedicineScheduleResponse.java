package com.example.onharu.medicineschedule.presentation.dto;

import com.example.onharu.medicineschedule.application.dto.MedicineScheduleResult;
import com.example.onharu.medicineschedule.domain.RepeatType;
import com.example.onharu.medicineschedule.domain.ScheduleType;
import java.time.LocalDate;
import java.time.LocalTime;

public record MedicineScheduleResponse(
        Long id,
        Long medicineId,
        ScheduleType scheduleType,
        RepeatType repeatType,
        Integer daysBitmask,
        LocalTime notifyTime,
        LocalDate startDate,
        LocalDate endDate
) {
    public static MedicineScheduleResponse from(MedicineScheduleResult result) {
        return new MedicineScheduleResponse(
                result.id(),
                result.medicineId(),
                result.scheduleType(),
                result.repeatType(),
                result.daysBitmask(),
                result.notifyTime(),
                result.startDate(),
                result.endDate()
        );
    }
}
