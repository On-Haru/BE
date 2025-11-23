package com.example.onharu.takinglog.application;

import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse.DayStatus;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse.DaySummary;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse.SlotSummary;
import com.example.onharu.takinglog.domain.TakingLog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TakingLogMonthlyCalculator {

    public TakingLogMonthlyResponse calculate(YearMonth yearMonth, List<TakingLog> logs,
            List<MedicineSchedule> schedules) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Map<LocalDate, List<TakingLog>> logsByDate = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getScheduledDateTime().toLocalDate()
                ));

        LocalDateTime now = LocalDateTime.now();
        List<DaySummary> days = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> buildDaySummary(date,
                        logsByDate.getOrDefault(date, List.of()), schedules, now))
                .collect(Collectors.toList());

        return new TakingLogMonthlyResponse(
                yearMonth.getYear(),
                yearMonth.getMonthValue(),
                TakingLogMonthlyResponse.defaultTimezone(),
                days
        );
    }

    private DaySummary buildDaySummary(LocalDate date, List<TakingLog> logs,
            List<MedicineSchedule> schedules, LocalDateTime now) {
        List<SlotSummary> slots = mergeSlots(date, logs, schedules, now)
                .stream()
                .sorted(Comparator.comparing(SlotSummary::scheduledDateTime))
                .collect(Collectors.toList());

        int requiredCount = slots.size();
        int takenCount = (int) slots.stream().filter(SlotSummary::taken).count();
        int takenRatio = requiredCount == 0 ? 0
                : Math.round((takenCount * 100f) / requiredCount);

        DayStatus status = determineDayStatus(requiredCount, takenCount, slots, now);

        return new DaySummary(date, requiredCount, takenCount, takenRatio, status, slots);
    }

    private DayStatus determineDayStatus(int requiredCount, int takenCount,
            List<SlotSummary> slots, LocalDateTime now) {
        if (requiredCount == 0) {
            return DayStatus.NONE;
        }
        if (takenCount == requiredCount) {
            return DayStatus.COMPLETE;
        }

        boolean hasFutureSlot = slots.stream()
                .anyMatch(slot -> !slot.scheduledDateTime().isBefore(now));

        if (takenCount == 0) {
            return hasFutureSlot ? DayStatus.PLANNED : DayStatus.MISSED;
        }

        return hasFutureSlot ? DayStatus.PARTIAL : DayStatus.MISSED;
    }

    private SlotSummary toSlotSummary(TakingLog log) {
        var schedule = log.getSchedule();
        return new SlotSummary(
                log.getId(),
                schedule.getId(),
                schedule.getMedicine().getName(),
                schedule.getScheduleType(),
                log.getScheduledDateTime(),
                log.isTaken(),
                log.getTakenDateTime(),
                log.getDelayMinutes()
        );
    }

    private List<SlotSummary> mergeSlots(LocalDate date, List<TakingLog> logs,
            List<MedicineSchedule> schedules, LocalDateTime now) {
        List<SlotSummary> slotSummaries = logs.stream()
                .map(this::toSlotSummary)
                .collect(Collectors.toList());

        Set<String> existingKeys = logs.stream()
                .map(log -> slotKey(log.getSchedule().getId(), log.getScheduledDateTime()))
                .collect(Collectors.toCollection(HashSet::new));

        for (MedicineSchedule schedule : schedules) {
            if (!isActiveOn(schedule, date)) {
                continue;
            }
            LocalDateTime scheduledDateTime = LocalDateTime.of(date, schedule.getNotifyTime());
            if (!scheduledDateTime.isAfter(now)) {
                continue;
            }
            String key = slotKey(schedule.getId(), scheduledDateTime);
            if (existingKeys.contains(key)) {
                continue;
            }

            slotSummaries.add(new SlotSummary(
                    null,
                    schedule.getId(),
                    schedule.getMedicine().getName(),
                    schedule.getScheduleType(),
                    scheduledDateTime,
                    false,
                    null,
                    null
            ));
        }

        return slotSummaries;
    }

    private boolean isActiveOn(MedicineSchedule schedule, LocalDate date) {
        if (schedule.getStartDate().isAfter(date)) {
            return false;
        }
        if (schedule.getEndDate() != null && schedule.getEndDate().isBefore(date)) {
            return false;
        }
        Integer bitmask = schedule.getDaysBitmask();
        if (bitmask == null) {
            return true;
        }
        int bit = 1 << (date.getDayOfWeek().getValue() % 7);
        return (bitmask & bit) != 0;
    }

    private String slotKey(Long scheduleId, LocalDateTime scheduledDateTime) {
        return scheduleId + "|" + scheduledDateTime.toString();
    }
}
