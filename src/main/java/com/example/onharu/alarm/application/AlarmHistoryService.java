package com.example.onharu.alarm.application;

import com.example.onharu.alarm.application.dto.AlarmHistoryCreateCommand;
import com.example.onharu.alarm.application.dto.AlarmHistoryResult;
import com.example.onharu.alarm.domain.AlarmHistory;
import com.example.onharu.alarm.domain.AlarmHistoryRepository;
import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.MedicineScheduleRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmHistoryService {

    private final AlarmHistoryRepository alarmHistoryRepository;
    private final MedicineScheduleRepository medicineScheduleRepository;

    @Transactional
    public AlarmHistoryResult recordHistory(AlarmHistoryCreateCommand command) {
        MedicineSchedule schedule = medicineScheduleRepository.findById(command.scheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICINE_SCHEDULE_NOT_FOUND));
        AlarmHistory history = AlarmHistory.create(
                schedule,
                command.channel(),
                command.sentAt(),
                command.success(),
                command.failureReason()
        );
        return AlarmHistoryResult.from(alarmHistoryRepository.save(history));
    }

    public AlarmHistoryResult getHistory(Long historyId) {
        return alarmHistoryRepository.findById(historyId)
                .map(AlarmHistoryResult::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALARM_HISTORY_NOT_FOUND));
    }

    public List<AlarmHistoryResult> getHistoriesBySchedule(Long scheduleId) {
        return alarmHistoryRepository.findByScheduleId(scheduleId)
                .stream()
                .map(AlarmHistoryResult::from)
                .collect(Collectors.toList());
    }
}
