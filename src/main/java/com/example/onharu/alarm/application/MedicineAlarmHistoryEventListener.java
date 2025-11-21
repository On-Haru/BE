package com.example.onharu.alarm.application;

import com.example.onharu.alarm.application.dto.AlarmHistoryCreateCommand;
import com.example.onharu.medicineschedule.application.event.MedicineAlarmEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MedicineAlarmHistoryEventListener {

    private final AlarmHistoryService alarmHistoryService;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(MedicineAlarmEvent event) {
        alarmHistoryService.recordHistory(AlarmHistoryCreateCommand.of(
                event.scheduleId(),
                event.channel(),
                event.triggeredAt(),
                event.success(),
                event.failureReason()
        ));
    }
}
