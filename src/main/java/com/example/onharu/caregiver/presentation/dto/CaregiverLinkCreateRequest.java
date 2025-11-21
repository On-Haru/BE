package com.example.onharu.caregiver.presentation.dto;

import com.example.onharu.caregiver.application.dto.CaregiverLinkCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CaregiverLinkCreateRequest(
        @NotBlank String phoneNumber,
        @NotNull Integer code
) {

    public CaregiverLinkCreateCommand toCommand(Long caregiverId) {
        return CaregiverLinkCreateCommand.of(phoneNumber, code, caregiverId);
    }
}
