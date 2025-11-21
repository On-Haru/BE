package com.example.onharu.caregiver.presentation.dto;

import com.example.onharu.caregiver.application.dto.CaregiverLinkResult;

public record CaregiverLinkResponse(
        Long id,
        Long caregiverId,
        Long seniorId
) {

    public static CaregiverLinkResponse from(CaregiverLinkResult result) {
        return new CaregiverLinkResponse(result.id(), result.caregiverId(), result.seniorId());
    }
}
