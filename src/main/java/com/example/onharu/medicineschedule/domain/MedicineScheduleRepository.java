package com.example.onharu.medicineschedule.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MedicineScheduleRepository {

    MedicineSchedule save(MedicineSchedule schedule);

    Optional<MedicineSchedule> findById(Long id);

    List<MedicineSchedule> findByMedicineId(Long medicineId);

    List<MedicineSchedule> findDueSchedules(LocalTime notifyTime, LocalDate today);

    void deleteByMedicineIdIn(List<Long> medicineIds);
}
