package com.example.onharu.prescription.infra.persistence;

import com.example.onharu.prescription.domain.Prescription;
import com.example.onharu.prescription.domain.PrescriptionRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrescriptionJpaRepository
        extends JpaRepository<Prescription, Long>, PrescriptionRepository {

    @Override
    @Query("select p from Prescription p where p.senior.id = :seniorId order by p.issuedDate desc")
    List<Prescription> findAllBySeniorIdOrderByIssuedDateDesc(@Param("seniorId") Long seniorId);
}
