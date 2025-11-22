package com.example.onharu.prescription.domain;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository {

    Prescription save(Prescription prescription);

    Optional<Prescription> findById(Long id);

    List<Prescription> findAllBySeniorIdOrderByIssuedDateDesc(Long seniorId);

    void delete(Prescription prescription);
}
