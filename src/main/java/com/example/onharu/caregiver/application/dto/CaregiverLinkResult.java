package com.example.onharu.caregiver.application.dto;

import com.example.onharu.caregiver.domain.CaregiverLink;

public record CaregiverLinkResult(
        Long id,
        Long caregiverId,
        Long seniorId
) {

    public static CaregiverLinkResult from(CaregiverLink link) {
        return new CaregiverLinkResult(
                link.getId(),
                link.getCaregiver().getId(),
                link.getSenior().getId()
        );
    }
}
