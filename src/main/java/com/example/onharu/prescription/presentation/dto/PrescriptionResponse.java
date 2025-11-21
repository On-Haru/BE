package com.example.onharu.prescription.presentation.dto;

import com.example.onharu.prescription.application.dto.PrescriptionResult;
import java.time.LocalDate;

public record PrescriptionResponse(
        Long id,
        Long seniorId,
        LocalDate issuedDate,
        String hospitalName,
        String doctorName,
        String note
) {
    public static PrescriptionResponse from(PrescriptionResult result) {
        return new PrescriptionResponse(
                result.id(),
                result.seniorId(),
                result.issuedDate(),
                result.hospitalName(),
                result.doctorName(),
                result.note()
        );
    }
}
