package com.example.onharu.prescription.infra.persistence;

import com.example.onharu.prescription.domain.Prescription;
import com.example.onharu.prescription.domain.PrescriptionRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionJpaRepository
        extends JpaRepository<Prescription, Long>, PrescriptionRepository {}
