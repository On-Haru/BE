package com.example.onharu.prescription.application.dto;

import com.example.onharu.medicine.application.dto.MedicineResult;
import com.example.onharu.prescription.domain.Prescription;
import java.time.LocalDate;
import java.util.List;

public record PrescriptionDetailResult(
        Long id,
        Long seniorId,
        LocalDate issuedDate,
        String hospitalName,
        String doctorName,
        String note,
        List<MedicineResult> medicines
) {

    public static PrescriptionDetailResult from(Prescription prescription,
            List<MedicineResult> medicines) {
        return new PrescriptionDetailResult(
                prescription.getId(),
                prescription.getSenior().getId(),
                prescription.getIssuedDate(),
                prescription.getHospitalName(),
                prescription.getDoctorName(),
                prescription.getNote(),
                List.copyOf(medicines)
        );
    }
}

