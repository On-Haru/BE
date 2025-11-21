package com.example.onharu.medicineschedule.application;

import com.example.onharu.medicineschedule.application.event.MedicineAlarmEvent;
import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.MedicineScheduleRepository;
import com.example.onharu.push.application.PushSubscriptionService;
import com.example.onharu.push.presentation.dto.NotifyRequest;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicineAlarmScheduler {

    private static final String CHANNEL_PUSH = "PUSH";

    private final MedicineScheduleRepository medicineScheduleRepository;
    private final PushSubscriptionService pushSubscriptionService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${scheduler.medicine-alarm.enabled:true}")
    private boolean schedulerEnabled;

    @Scheduled(cron = "${scheduler.medicine-alarm.cron:0 * * * * *}")
    @Transactional(readOnly = true)
    public void publishDueAlarms() {
        if (!schedulerEnabled) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalTime currentSlot = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime scheduledDateTime = LocalDateTime.of(today, currentSlot);
        DayOfWeek todayOfWeek = today.getDayOfWeek();

        log.info("Checking medicine schedules for {} at {}", today, currentSlot);
        List<MedicineSchedule> schedules = medicineScheduleRepository.findDueSchedules(
                currentSlot,
                today
        );

        log.info("스케쥴 개수: {}", schedules.size());

        for (MedicineSchedule schedule : schedules) {
            if (!isDayEnabled(schedule, todayOfWeek)) {
                continue;
            }

            Long userId = schedule.getMedicine()
                    .getPrescription()
                    .getSenior()
                    .getId();

            String title = String.format("[복약 알림] %s", schedule.getMedicine().getName());
            String body = String.format("%s 복약 시간입니다. 지금 복약을 확인해주세요.",
                    schedule.getScheduleType().name());

            boolean success;
            String failureReason = null;
            try {
                success = pushSubscriptionService.sendNotification(new NotifyRequest(title, body),
                        userId);
                if (!success) {
                    failureReason = "No active push subscription";
                }
            } catch (Exception e) {
                success = false;
                failureReason = e.getMessage();
                log.warn("Failed to send reminder for schedule {}: {}", schedule.getId(),
                        e.getMessage());
            }

            MedicineAlarmEvent event = MedicineAlarmEvent.of(
                    schedule.getId(),
                    userId,
                    scheduledDateTime,
                    LocalDateTime.now(),
                    CHANNEL_PUSH,
                    success,
                    failureReason
            );
            eventPublisher.publishEvent(event);
        }
    }

    private boolean isDayEnabled(MedicineSchedule schedule, DayOfWeek dayOfWeek) {
        Integer bitmask = schedule.getDaysBitmask();
        if (bitmask == null) {
            return true;
        }
        int bit = 1 << (dayOfWeek.getValue() % 7);
        return (bitmask & bit) != 0;
    }
}
