package com.example.onharu.prescription.application.dto;

import java.time.LocalDate;

public record PrescriptionCreateCommand(
        Long seniorId,
        LocalDate issuedDate,
        LocalDate expiredDate,
        String doctorName,
        String note
) {
    public static PrescriptionCreateCommand of(Long seniorId, LocalDate issuedDate, LocalDate expiredDate, String doctorName, String note) {
        return new PrescriptionCreateCommand(seniorId, issuedDate, expiredDate, doctorName, note);
    }
}
