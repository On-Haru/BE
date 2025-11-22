package com.example.onharu.report.domain;

import java.util.Optional;

public interface ReportRepository {

    Report save(Report report);

    Optional<Report> findLatestBySeniorId(Long seniorId);

    Optional<Report> findBySeniorIdAndReportDate(Long seniorId, java.time.LocalDate reportDate);
}
