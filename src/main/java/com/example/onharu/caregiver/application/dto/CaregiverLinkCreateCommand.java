package com.example.onharu.caregiver.application.dto;

public record CaregiverLinkCreateCommand(
        String phone,
        Integer code,
        Long caregiverId
) {

    public static CaregiverLinkCreateCommand of(String phone, Integer code, Long seniorId) {
        return new CaregiverLinkCreateCommand(phone, code, seniorId);
    }
}
