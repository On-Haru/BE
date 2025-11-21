package com.example.onharu.alarm.infra.persistence;

import com.example.onharu.alarm.domain.AlarmHistory;
import com.example.onharu.alarm.domain.AlarmHistoryRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmHistoryJpaRepository
        extends JpaRepository<AlarmHistory, Long>, AlarmHistoryRepository {

    List<AlarmHistory> findByScheduleId(Long scheduleId);
}
