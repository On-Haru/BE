package com.example.onharu.takinglog.application.dto;

import com.example.onharu.medicineschedule.domain.ScheduleType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TakingLogMonthlyResponse(
        int year,
        int month,
        String timezone,
        List<DaySummary> days
) {

    private static final String DEFAULT_TIMEZONE = "Asia/Seoul";

    public static TakingLogMonthlyResponse empty(int year, int month) {
        return new TakingLogMonthlyResponse(year, month, DEFAULT_TIMEZONE, List.of());
    }

    public static String defaultTimezone() {
        return DEFAULT_TIMEZONE;
    }

    public record DaySummary(
            LocalDate date,
            int requiredCount,
            int takenCount,
            int takenRatio,
            DayStatus status,
            List<SlotSummary> slots
    ) {
    }

    public record SlotSummary(
            Long slotId,
            Long scheduleId,
            String medicineName,
            ScheduleType scheduleType,
            LocalDateTime scheduledDateTime,
            boolean taken,
            LocalDateTime takenDateTime,
            Integer delayMinutes
    ) {
    }

    public enum DayStatus {
        NONE,
        PLANNED,
        PARTIAL,
        COMPLETE,
        MISSED
    }
}
