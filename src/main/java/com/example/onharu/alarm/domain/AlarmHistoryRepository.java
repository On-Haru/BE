package com.example.onharu.alarm.domain;

import java.util.List;
import java.util.Optional;

public interface AlarmHistoryRepository {

    AlarmHistory save(AlarmHistory history);

    Optional<AlarmHistory> findById(Long id);

    List<AlarmHistory> findByScheduleId(Long scheduleId);
}
