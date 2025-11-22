package com.example.onharu.caregiver.infra.persistence;

import com.example.onharu.caregiver.domain.CaregiverLink;
import com.example.onharu.caregiver.domain.CaregiverLinkRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CaregiverLinkJpaRepository
        extends JpaRepository<CaregiverLink, Long>, CaregiverLinkRepository {

    @Query("SELECT COUNT(cl) > 0 FROM CaregiverLink cl WHERE cl.caregiver.id = :caregiverId AND cl.senior.id = :seniorId")
    boolean alreadyLinkExists(@Param("caregiverId") Long caregiverId,
            @Param("seniorId") Long seniorId);

    @Query("SELECT cl FROM CaregiverLink cl WHERE cl.caregiver.id = :caregiverId")
    List<CaregiverLink> findAllByCaregiverId(@Param("caregiverId") Long caregiverId);

    Boolean existsBySeniorId(Long seniorId);
}
