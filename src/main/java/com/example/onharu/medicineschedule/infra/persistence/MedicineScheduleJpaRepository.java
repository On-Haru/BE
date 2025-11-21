package com.example.onharu.medicineschedule.infra.persistence;

import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.MedicineScheduleRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicineScheduleJpaRepository
        extends JpaRepository<MedicineSchedule, Long>, MedicineScheduleRepository {

    List<MedicineSchedule> findByMedicineId(Long medicineId);

    @Override
    @Query("SELECT ms FROM MedicineSchedule ms " +
            "WHERE ms.notifyTime = :notifyTime " +
            "AND ms.startDate <= :today " +
            "AND (ms.endDate IS NULL OR ms.endDate >= :today)")
    List<MedicineSchedule> findDueSchedules(@Param("notifyTime") LocalTime notifyTime,
            @Param("today") LocalDate today);
}
