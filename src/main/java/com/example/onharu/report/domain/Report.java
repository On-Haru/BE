package com.example.onharu.report.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "senior_id", nullable = false)
    private Long seniorId;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false, length = 20)
    private ReportPeriodType periodType;

    @Column(name = "json_payload", nullable = false, columnDefinition = "TEXT")
    private String jsonPayload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private Report(Long seniorId, LocalDate reportDate, ReportPeriodType periodType, String jsonPayload) {
        this.seniorId = seniorId;
        this.reportDate = reportDate;
        this.periodType = periodType;
        this.jsonPayload = jsonPayload;
        this.createdAt = LocalDateTime.now();
    }

    public static Report create(Long seniorId, LocalDate reportDate, ReportPeriodType periodType,
            String jsonPayload) {
        return new Report(seniorId, reportDate, periodType, jsonPayload);
    }

    public void updatePayload(String jsonPayload) {
        this.jsonPayload = jsonPayload;
        this.createdAt = LocalDateTime.now();
    }
}
