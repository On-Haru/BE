package com.example.onharu.takinglog.application;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.MedicineScheduleRepository;
import com.example.onharu.takinglog.application.dto.TakingLogCreateCommand;
import com.example.onharu.takinglog.application.dto.TakingLogResult;
import com.example.onharu.takinglog.domain.TakingLog;
import com.example.onharu.takinglog.domain.TakingLogRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TakingLogService {

    private final TakingLogRepository takingLogRepository;
    private final MedicineScheduleRepository medicineScheduleRepository;

    @Transactional
    public TakingLogResult recordTaking(TakingLogCreateCommand command) {
        MedicineSchedule schedule = medicineScheduleRepository.findById(command.scheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICINE_SCHEDULE_NOT_FOUND));
        TakingLog takingLog = TakingLog.create(
                schedule,
                command.scheduledDateTime(),
                command.takenDateTime(),
                command.taken(),
                command.delayMinutes()
        );
        return TakingLogResult.from(takingLogRepository.save(takingLog));
    }

    public TakingLogResult getLog(Long logId) {
        return takingLogRepository.findById(logId)
                .map(TakingLogResult::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.TAKING_LOG_NOT_FOUND));
    }

    public List<TakingLogResult> getLogsBySchedule(Long scheduleId) {
        return takingLogRepository.findByScheduleId(scheduleId)
                .stream()
                .map(TakingLogResult::from)
                .collect(Collectors.toList());
    }
}
