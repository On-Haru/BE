package com.example.onharu.prescription.domain;

import java.util.Optional;

public interface PrescriptionRepository {

    Prescription save(Prescription prescription);

    Optional<Prescription> findById(Long id);
}
