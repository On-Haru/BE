package com.example.onharu.caregiver.domain;

import java.util.List;
import java.util.Optional;

public interface CaregiverLinkRepository {

    CaregiverLink save(CaregiverLink link);

    Optional<CaregiverLink> findById(Long id);

    boolean alreadyLinkExists(Long caregiverId, Long seniorId);

    List<CaregiverLink> findAllByCaregiverId(Long caregiverId);
}
