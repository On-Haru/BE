package com.example.onharu.report.application;

import com.example.onharu.medicineschedule.domain.ScheduleType;
import com.example.onharu.report.application.dto.ReportPayload;
import com.example.onharu.report.application.dto.ReportPayload.ChartData;
import com.example.onharu.report.application.dto.ReportPayload.ChartData.MedicinePatternEntry;
import com.example.onharu.report.application.dto.ReportPayload.ChartData.Status;
import com.example.onharu.report.application.dto.ReportPayload.ChartData.TimePatternEntry;
import com.example.onharu.report.application.dto.ReportPayload.ComparisonRate;
import com.example.onharu.report.application.dto.ReportPayload.Direction;
import com.example.onharu.report.domain.ReportPeriodType;
import com.example.onharu.takinglog.domain.TakingLog;
import com.example.onharu.takinglog.domain.TakingLogRepository;
import com.example.onharu.user.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final TakingLogRepository takingLogRepository;

    public ReportPayload buildPayload(User senior, YearMonth targetMonth) {
        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<TakingLog> logs = takingLogRepository.findBySeniorIdAndScheduledDateBetween(
                senior.getId(), start, end);

        int totalCount = logs.size();
        int takenCount = (int) logs.stream().filter(TakingLog::isTaken).count();
        int overallRate = percent(takenCount, totalCount);

        ComparisonRate comparisonRate = buildComparisonRate(senior.getId(), targetMonth, overallRate);
        Integer avgDelay = averageDelay(logs);
        Integer missedCount = totalCount == 0 ? null : totalCount - takenCount;

        List<TimePatternEntry> timePatterns = buildTimePatterns(logs);
        List<MedicinePatternEntry> medicinePatterns = buildMedicinePatterns(logs);

        ReportPeriodType periodType = determinePeriodType(logs);
        String title = String.format("%s %d년 %02d월 복약 리포트", senior.getName(),
                targetMonth.getYear(), targetMonth.getMonthValue());

        return ReportPayload.createBase(
                startDate,
                endDate,
                periodType,
                title,
                overallRate,
                comparisonRate,
                avgDelay,
                missedCount,
                timePatterns,
                medicinePatterns
        );
    }

    private List<TimePatternEntry> buildTimePatterns(List<TakingLog> logs) {
        Map<ScheduleType, List<TakingLog>> grouped = new EnumMap<>(ScheduleType.class);
        for (TakingLog log : logs) {
            grouped.computeIfAbsent(log.getSchedule().getScheduleType(), key -> new ArrayList<>())
                    .add(log);
        }

        List<TimePatternEntry> entries = new ArrayList<>();
        entries.add(buildTimeEntry("아침", grouped.get(ScheduleType.MORNING)));
        entries.add(buildTimeEntry("점심", grouped.get(ScheduleType.LUNCH)));
        entries.add(buildTimeEntry("저녁", grouped.get(ScheduleType.EVENING)));
        return entries;
    }

    private TimePatternEntry buildTimeEntry(String label, List<TakingLog> logs) {
        int rate = percentTaken(logs);
        Status status = statusForRate(rate);
        return new TimePatternEntry(label, rate, status);
    }

    private Status statusForRate(int rate) {
        if (rate >= 80) {
            return Status.GOOD;
        }
        if (rate >= 50) {
            return Status.WARN;
        }
        return Status.BAD;
    }

    private List<MedicinePatternEntry> buildMedicinePatterns(List<TakingLog> logs) {
        Map<String, List<TakingLog>> grouped = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getSchedule().getMedicine().getName(),
                        Collectors.toList()));

        return grouped.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<TakingLog> medicineLogs = entry.getValue();
                    int adherence = percentTaken(medicineLogs);
                    return new MedicinePatternEntry(name, adherence, "분석 결과를 준비 중입니다.");
                })
                .sorted(Comparator.comparing(MedicinePatternEntry::medicineName,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private ComparisonRate buildComparisonRate(Long seniorId, YearMonth targetMonth, int currentRate) {
        YearMonth previous = targetMonth.minusMonths(1);
        LocalDateTime prevStart = previous.atDay(1).atStartOfDay();
        LocalDateTime prevEnd = previous.plusMonths(1).atDay(1).atStartOfDay();
        List<TakingLog> previousLogs = takingLogRepository.findBySeniorIdAndScheduledDateBetween(
                seniorId, prevStart, prevEnd);
        if (previousLogs.isEmpty()) {
            return null;
        }
        int previousRate = percentTaken(previousLogs);
        int diff = Math.abs(currentRate - previousRate);
        Direction direction = currentRate >= previousRate ? Direction.UP : Direction.DOWN;
        if (diff == 0) {
            direction = Direction.UP;
        }
        return new ComparisonRate(diff, direction);
    }

    private ReportPeriodType determinePeriodType(List<TakingLog> logs) {
        if (logs.isEmpty()) {
            return ReportPeriodType.ONBOARDING;
        }
        LocalDate earliest = logs.stream()
                .map(log -> log.getScheduledDateTime().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
        LocalDate latest = logs.stream()
                .map(log -> log.getScheduledDateTime().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(earliest);
        long days = ChronoUnit.DAYS.between(earliest, latest) + 1;
        return days < 7 ? ReportPeriodType.ONBOARDING : ReportPeriodType.MONTHLY;
    }

    private Integer averageDelay(List<TakingLog> logs) {
        List<Integer> delays = logs.stream()
                .map(TakingLog::getDelayMinutes)
                .filter(value -> value != null && value >= 0)
                .toList();
        if (delays.isEmpty()) {
            return null;
        }
        double average = delays.stream().mapToInt(Integer::intValue).average().orElse(0);
        return (int) Math.round(average);
    }

    private int percentTaken(List<TakingLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return 0;
        }
        int taken = (int) logs.stream().filter(TakingLog::isTaken).count();
        return percent(taken, logs.size());
    }

    private int percent(int numerator, int denominator) {
        if (denominator == 0) {
            return 0;
        }
        return (int) Math.round((numerator * 100.0) / denominator);
    }
}
