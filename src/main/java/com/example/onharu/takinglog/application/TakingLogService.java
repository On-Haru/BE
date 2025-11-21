package com.example.onharu.takinglog.application;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.MedicineScheduleRepository;
import com.example.onharu.takinglog.application.dto.TakenLogCommand;
import com.example.onharu.takinglog.application.dto.TakingLogCreateCommand;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyRequest;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse.DayStatus;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse.DaySummary;
import com.example.onharu.takinglog.application.dto.TakingLogMonthlyResponse.SlotSummary;
import com.example.onharu.takinglog.application.dto.TakingLogSlotDto;
import com.example.onharu.takinglog.domain.TakingLog;
import com.example.onharu.takinglog.domain.TakingLogRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

        YearMonth yearMonth = YearMonth.of(request.year(), request.month());
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endExclusive = endDate.plusDays(1).atStartOfDay();

        List<TakingLog> logs = takingLogRepository.findBySeniorIdAndScheduledDateBetween(
                seniorId,
                startDateTime,
                endExclusive
        );

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
                ZoneId.systemDefault().getId(),
                days
        );
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
        TakingLogSlotDto dto = TakingLogSlotDto.from(log);
        return new SlotSummary(
                dto.id(),
                dto.scheduleId(),
                dto.medicineName(),
                dto.scheduleType(),
                dto.scheduledDateTime(),
                dto.taken(),
                dto.takenDateTime(),
                dto.delayMinutes()
        );
    }
}
