package com.example.onharu.takinglog.infra.persistence;

import com.example.onharu.takinglog.domain.TakingLog;
import com.example.onharu.takinglog.domain.TakingLogRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TakingLogJpaRepository extends JpaRepository<TakingLog, Long>, TakingLogRepository {

    List<TakingLog> findByScheduleId(Long scheduleId);
}
