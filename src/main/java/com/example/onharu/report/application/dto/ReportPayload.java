package com.example.onharu.report.application.dto;

import com.example.onharu.report.domain.ReportPeriodType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record ReportPayload(
        ReportMeta reportMeta,
        AiAnalysis aiAnalysis,
        Statistics statistics,
        ChartData chartData
) {

    public static ReportPayload createBase(LocalDate startDate, LocalDate endDate,
            ReportPeriodType periodType, String title,
            int overallRate, ComparisonRate comparisonRate,
            Integer averageDelayMinutes, Integer missedCount,
            List<ChartData.TimePatternEntry> timePatterns,
            List<ChartData.MedicinePatternEntry> medicinePatterns) {
        ReportMeta meta = new ReportMeta(null, title, periodType, formatRange(startDate, endDate));
        AiAnalysis ai = new AiAnalysis("", null, Collections.emptyList());
        Statistics stats = new Statistics(overallRate, comparisonRate, averageDelayMinutes,
                missedCount);
        ChartData chart = new ChartData(timePatterns, medicinePatterns);
        return new ReportPayload(meta, ai, stats, chart);
    }

    public ReportPayload withReportId(Long reportId) {
        ReportMeta meta = new ReportMeta(reportId, reportMeta.title(), reportMeta.periodType(),
                reportMeta.dateRange());
        return new ReportPayload(meta, aiAnalysis, statistics, chartData);
    }

    public ReportPayload withAiInsights(String summary, String suggestion, List<String> riskTags,
            Map<String, String> medicineComments) {
        AiAnalysis ai = new AiAnalysis(summary, suggestion, riskTags == null ? List.of() : riskTags);
        List<ChartData.MedicinePatternEntry> updated = chartData.medicinePattern().stream()
                .map(entry -> new ChartData.MedicinePatternEntry(
                        entry.medicineName(),
                        entry.rate(),
                        medicineComments == null
                                ? entry.aiComment()
                                : medicineComments.getOrDefault(entry.medicineName(),
                                        entry.aiComment())
                ))
                .toList();
        ChartData chart = new ChartData(chartData.timePattern(), updated);
        return new ReportPayload(reportMeta, ai, statistics, chart);
    }

    private static String formatRange(LocalDate start, LocalDate end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return formatter.format(start) + " ~ " + formatter.format(end);
    }

    public record ReportMeta(
            Long reportId,
            String title,
            ReportPeriodType periodType,
            String dateRange
    ) {
    }

    public record AiAnalysis(
            String summary,
            String suggestion,
            List<String> riskTags
    ) {
    }

    public record Statistics(
            int overallRate,
            ComparisonRate comparisonRate,
            Integer averageDelayMinutes,
            Integer missedCount
    ) {
    }

    public record ComparisonRate(
            int diff,
            Direction direction
    ) {
    }

    public enum Direction {
        UP,
        DOWN
    }

    public record ChartData(
            List<TimePatternEntry> timePattern,
            List<MedicinePatternEntry> medicinePattern
    ) {

        public record TimePatternEntry(
                String label,
                int rate,
                Status status
        ) {
        }

        public record MedicinePatternEntry(
                String medicineName,
                int rate,
                String aiComment
        ) {
        }

        public enum Status {
            GOOD,
            WARN,
            BAD
        }
    }
}
