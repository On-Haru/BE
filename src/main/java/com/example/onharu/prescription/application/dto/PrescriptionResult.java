package com.example.onharu.prescription.application.dto;

import com.example.onharu.prescription.domain.Prescription;
import java.time.LocalDate;

public record PrescriptionResult(
        Long id,
        Long seniorId,
        LocalDate issuedDate,
        LocalDate expiredDate,
        String doctorName,
        String note
) {
    public static PrescriptionResult from(Prescription prescription) {
        return new PrescriptionResult(
                prescription.getId(),
                prescription.getSenior().getId(),
                prescription.getIssuedDate(),
                prescription.getExpiredDate(),
                prescription.getDoctorName(),
                prescription.getNote()
        );
    }
}
