package com.example.onharu.prescription.application.dto;

import com.example.onharu.prescription.domain.Prescription;
import java.time.LocalDate;

public record PrescriptionResult(
        Long id,
        Long seniorId,
        LocalDate issuedDate,
        String hospitalName,
        String doctorName,
        String note
) {
    public static PrescriptionResult from(Prescription prescription) {
        return new PrescriptionResult(
                prescription.getId(),
                prescription.getSenior().getId(),
                prescription.getIssuedDate(),
                prescription.getHospitalName(),
                prescription.getDoctorName(),
                prescription.getNote()
        );
    }
}
