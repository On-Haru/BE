package com.example.onharu.takinglog.application;

import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse.DayStatus;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse.DaySummary;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse.SlotSummary;
import com.example.onharu.takinglog.domain.TakingLog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TakingLogMonthlyCalculator {

    public TakingLogMonthlyResponse calculate(YearMonth yearMonth, List<TakingLog> logs) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Map<LocalDate, List<TakingLog>> logsByDate = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getScheduledDateTime().toLocalDate()
                ));

        LocalDateTime now = LocalDateTime.now();
        List<DaySummary> days = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> buildDaySummary(date,
                        logsByDate.getOrDefault(date, List.of()), now))
                .collect(Collectors.toList());

        return new TakingLogMonthlyResponse(
                yearMonth.getYear(),
                yearMonth.getMonthValue(),
                TakingLogMonthlyResponse.defaultTimezone(),
                days
        );
    }

    private DaySummary buildDaySummary(LocalDate date, List<TakingLog> logs, LocalDateTime now) {
        int requiredCount = logs.size();
        int takenCount = (int) logs.stream().filter(TakingLog::isTaken).count();
        int takenRatio = requiredCount == 0 ? 0
                : Math.round((takenCount * 100f) / requiredCount);

        List<SlotSummary> slots = logs.stream()
                .sorted(Comparator.comparing(TakingLog::getScheduledDateTime))
                .map(this::toSlotSummary)
                .collect(Collectors.toList());

        DayStatus status = determineDayStatus(requiredCount, takenCount, logs, now);

        return new DaySummary(date, requiredCount, takenCount, takenRatio, status, slots);
    }

    private DayStatus determineDayStatus(int requiredCount, int takenCount,
            List<TakingLog> logs, LocalDateTime now) {
        if (requiredCount == 0) {
            return DayStatus.NONE;
        }
        if (takenCount == requiredCount) {
            return DayStatus.COMPLETE;
        }

        boolean hasFutureSlot = logs.stream()
                .anyMatch(log -> !log.getScheduledDateTime().isBefore(now));

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
}
