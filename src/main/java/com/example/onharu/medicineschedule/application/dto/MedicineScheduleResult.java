package com.example.onharu.medicineschedule.application.dto;

import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.RepeatType;
import com.example.onharu.medicineschedule.domain.ScheduleType;
import java.time.LocalDate;
import java.time.LocalTime;

public record MedicineScheduleResult(
        Long id,
        Long medicineId,
        ScheduleType scheduleType,
        RepeatType repeatType,
        Integer daysBitmask,
        LocalTime notifyTime,
        LocalDate startDate,
        LocalDate endDate
) {
    public static MedicineScheduleResult from(MedicineSchedule schedule) {
        return new MedicineScheduleResult(
                schedule.getId(),
                schedule.getMedicine().getId(),
                schedule.getScheduleType(),
                schedule.getRepeatType(),
                schedule.getDaysBitmask(),
                schedule.getNotifyTime(),
                schedule.getStartDate(),
                schedule.getEndDate()
        );
    }
}
