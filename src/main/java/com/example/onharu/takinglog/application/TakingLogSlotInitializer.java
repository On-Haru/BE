package com.example.onharu.takinglog.application;

import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.takinglog.domain.TakingLog;
import com.example.onharu.takinglog.domain.TakingLogRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TakingLogSlotInitializer {

    private final TakingLogRepository takingLogRepository;

    @Transactional
    public void backfillPastSlots(MedicineSchedule schedule) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDate lastDate = determineLastDate(schedule, now.toLocalDate());

        if (schedule.getStartDate().isAfter(lastDate)) {
            log.debug("No past slots to backfill for schedule {}", schedule.getId());
            return;
        }

        for (LocalDate date = schedule.getStartDate(); !date.isAfter(lastDate); date = date.plusDays(1)) {
            if (!isDayEnabled(schedule, date)) {
                continue;
            }

            LocalDateTime scheduledDateTime = LocalDateTime.of(date, schedule.getNotifyTime());
            if (!scheduledDateTime.isBefore(now)) {
                continue;
            }

            boolean exists = takingLogRepository.findByScheduleIdAndScheduledDateTime(
                    schedule.getId(), scheduledDateTime).isPresent();
            if (exists) {
                continue;
            }

            TakingLog slot = TakingLog.create(schedule, scheduledDateTime, null, false, null);
            takingLogRepository.save(slot);
            log.info("Backfilled taking log slot for schedule {} at {}", schedule.getId(), scheduledDateTime);
        }
    }

    private LocalDate determineLastDate(MedicineSchedule schedule, LocalDate today) {
        LocalDate endDate = schedule.getEndDate();
        if (endDate == null || endDate.isAfter(today)) {
            return today;
        }
        return endDate;
    }

    private boolean isDayEnabled(MedicineSchedule schedule, LocalDate date) {
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
}

