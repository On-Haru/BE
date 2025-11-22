package com.example.onharu.medicine.infra.persistence;

import com.example.onharu.medicine.domain.Medicine;
import com.example.onharu.medicine.domain.MedicineRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineJpaRepository extends JpaRepository<Medicine, Long>, MedicineRepository {

    List<Medicine> findByPrescriptionId(Long prescriptionId);

    void deleteAllByPrescriptionId(Long prescriptionId);
}
