package com.example.onharu.takinglog.infra.persistence;

import com.example.onharu.takinglog.domain.TakingLog;
import com.example.onharu.takinglog.domain.TakingLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TakingLogJpaRepository extends JpaRepository<TakingLog, Long>, TakingLogRepository {

    List<TakingLog> findByScheduleId(Long scheduleId);

    @EntityGraph(attributePaths = {"schedule", "schedule.medicine", "schedule.medicine.prescription"})
    List<TakingLog> findAllBySchedule_Medicine_Prescription_Senior_IdAndScheduledDateTimeBetween(
            Long seniorId,
            LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    @Override
    default List<TakingLog> findBySeniorIdAndScheduledDateBetween(Long seniorId,
            LocalDateTime startInclusive, LocalDateTime endExclusive) {
        return findAllBySchedule_Medicine_Prescription_Senior_IdAndScheduledDateTimeBetween(
                seniorId, startInclusive, endExclusive);
    }
}
