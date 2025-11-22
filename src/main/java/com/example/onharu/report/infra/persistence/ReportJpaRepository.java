package com.example.onharu.report.infra.persistence;

import com.example.onharu.report.domain.Report;
import com.example.onharu.report.domain.ReportRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportJpaRepository extends JpaRepository<Report, Long>, ReportRepository {

    Optional<Report> findTopBySeniorIdOrderByCreatedAtDesc(Long seniorId);

    Optional<Report> findBySeniorIdAndReportDate(Long seniorId, java.time.LocalDate reportDate);

    @Override
    default Optional<Report> findLatestBySeniorId(Long seniorId) {
        return findTopBySeniorIdOrderByCreatedAtDesc(seniorId);
    }
}
