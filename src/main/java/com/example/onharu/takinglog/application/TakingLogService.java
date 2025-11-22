package com.example.onharu.takinglog.application;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.MedicineScheduleRepository;
import com.example.onharu.takinglog.application.dto.TakenLogCommand;
import com.example.onharu.takinglog.application.dto.TakingLogCreateCommand;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyRequest;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse;
import com.example.onharu.takinglog.application.dto.TakingLogSlotDto;
import com.example.onharu.takinglog.domain.TakingLog;
import com.example.onharu.takinglog.domain.TakingLogRepository;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
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
    private final TakingLogMonthlyCalculator takingLogMonthlyCalculator;

    @Transactional
    public TakingLogSlotDto recordTaking(TakingLogCreateCommand command) {
        MedicineSchedule schedule = medicineScheduleRepository.findById(
                        command.slotKey().scheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICINE_SCHEDULE_NOT_FOUND));
        TakingLog takingLog = TakingLog.create(
                schedule,
                command.slotKey().scheduledDateTime(),
                command.takenDateTime(),
                command.taken(),
                command.delayMinutes()
        );

        return TakingLogSlotDto.from(takingLogRepository.save(takingLog));
    }

    public TakingLogSlotDto getLog(Long logId) {
        return takingLogRepository.findById(logId)
                .map(TakingLogSlotDto::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.TAKING_LOG_NOT_FOUND));
    }

    public List<TakingLogSlotDto> getLogsBySchedule(Long scheduleId) {
        return takingLogRepository.findByScheduleId(scheduleId)
                .stream()
                .map(TakingLogSlotDto::from)
                .collect(Collectors.toList());
    }

    public TakingLogMonthlyResponse getMonthlyCalendar(TakingLogMonthlyRequest request) {
        Long seniorId = Optional.ofNullable(request.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        YearMonth yearMonth = request.toYearMonth();

        var startDateTime = yearMonth.atDay(1).atStartOfDay();
        var endExclusive = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay();

        List<TakingLog> logs = takingLogRepository.findBySeniorIdAndScheduledDateBetween(
                seniorId,
                startDateTime,
                endExclusive
        );

        return takingLogMonthlyCalculator.calculate(yearMonth, logs);
    }

    @Transactional
    public void hasTaken(TakenLogCommand command) {
        medicineScheduleRepository.findById(command.slotKey().scheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICINE_SCHEDULE_NOT_FOUND));

        TakingLog takingLog = takingLogRepository.findByScheduleIdAndScheduledDateTime(
                        command.slotKey().scheduleId(), command.slotKey().scheduledDateTime())
                .orElseThrow(() -> new BusinessException(ErrorCode.TAKING_LOG_NOT_FOUND));

        if (command.taken()) {
            takingLog.markAsTaken();
        }

        takingLogRepository.save(takingLog);
    }

}
