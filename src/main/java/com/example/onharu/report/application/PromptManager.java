package com.example.onharu.report.application;

import com.example.onharu.global.client.ai.dto.ReportPayload;
import java.time.YearMonth;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PromptManager {

    public String buildPrompt(ReportPayload payload, YearMonth month) {
        StringBuilder builder = new StringBuilder();
        builder.append("당신은 복약 관리 전문가입니다. 아래 데이터로 월간 리포트를 작성하세요.\\n")
                .append("1) 전체 총평 2) 행동 제안 3) 위험 신호 태그 4) 약별 한 줄 코멘트를 포함하세요.\\n");

        builder.append(String.format("보고 월: %s\\n", month));
        builder.append(String.format("리포트 제목: %s\\n", payload.reportMeta().title()));
        builder.append(String.format("기간: %s\\n", payload.reportMeta().dateRange()));

        var stats = payload.statistics();
        builder.append(String.format("전체 복약률: %d%%\\n", stats.overallRate()));
        if (stats.comparisonRate() != null) {
            builder.append(String.format("전월 대비: %s %d%%p\\n",
                    stats.comparisonRate().direction(), stats.comparisonRate().diff()));
        }
        if (stats.averageDelayMinutes() != null) {
            builder.append(String.format("평균 지연: %d분\\n", stats.averageDelayMinutes()));
        }
        if (stats.missedCount() != null) {
            builder.append(String.format("미복용 횟수: %d\\n", stats.missedCount()));
        }

        var timePattern = payload.chartData().timePattern().stream()
                .map(entry -> String.format("%s:%d%%(%s)", entry.label(), entry.rate(),
                        entry.status()))
                .collect(Collectors.joining(", "));
        builder.append("시간대별 복약률: ").append(timePattern).append("\\n");

        var medicinePattern = payload.chartData().medicinePattern().stream()
                .map(entry -> String.format("%s:%d%%", entry.medicineName(), entry.rate()))
                .collect(Collectors.joining(", "));
        builder.append("약별 복약률: ").append(medicinePattern).append("\\n");

        builder.append("지연, 누락, 저녁/주말 패턴 등을 분석해 riskTags를 제안하세요.");
        return builder.toString();
    }
}
