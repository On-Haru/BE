package com.example.onharu.medicine.domain;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository {

    Medicine save(Medicine medicine);

    Optional<Medicine> findById(Long id);

    List<Medicine> findByPrescriptionId(Long prescriptionId);
}
