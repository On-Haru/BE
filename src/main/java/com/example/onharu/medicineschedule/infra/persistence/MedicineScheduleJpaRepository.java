package com.example.onharu.medicineschedule.infra.persistence;

import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.MedicineScheduleRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineScheduleJpaRepository
        extends JpaRepository<MedicineSchedule, Long>, MedicineScheduleRepository {

    List<MedicineSchedule> findByMedicineId(Long medicineId);
}
