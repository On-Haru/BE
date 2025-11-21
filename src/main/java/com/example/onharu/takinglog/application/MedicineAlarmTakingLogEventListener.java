package com.example.onharu.takinglog.application;

import com.example.onharu.medicineschedule.application.event.MedicineAlarmEvent;
import com.example.onharu.takinglog.application.dto.TakingLogCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MedicineAlarmTakingLogEventListener {

    private final TakingLogService takingLogService;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(MedicineAlarmEvent event) {
        takingLogService.recordTaking(TakingLogCreateCommand.of(
                event.scheduleId(),
                event.scheduledDateTime(),
                null,
                false,
                null
        ));
    }
}
