package com.example.onharu.prescription.presentation.dto;

import com.example.onharu.medicine.presentation.dto.MedicineResponse;
import com.example.onharu.prescription.application.dto.PrescriptionDetailResult;
import com.example.onharu.prescription.application.dto.PrescriptionResult;
import java.time.LocalDate;
import java.util.List;

public record PrescriptionResponse(
        Long id,
        Long seniorId,
        LocalDate issuedDate,
        String hospitalName,
        String doctorName,
        String note,
        List<MedicineResponse> medicines
) {
    public static PrescriptionResponse from(PrescriptionResult result) {
        return new PrescriptionResponse(
                result.id(),
                result.seniorId(),
                result.issuedDate(),
                result.hospitalName(),
                result.doctorName(),
                result.note(),
                List.of()
        );
    }

    public static PrescriptionResponse fromDetail(PrescriptionDetailResult result) {
        List<MedicineResponse> medicines = result.medicines().stream()
                .map(MedicineResponse::from)
                .toList();
        return new PrescriptionResponse(
                result.id(),
                result.seniorId(),
                result.issuedDate(),
                result.hospitalName(),
                result.doctorName(),
                result.note(),
                medicines
        );
    }
}
