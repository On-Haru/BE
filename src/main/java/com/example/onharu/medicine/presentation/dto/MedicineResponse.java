package com.example.onharu.medicine.presentation.dto;

import com.example.onharu.medicine.application.dto.MedicineResult;

public record MedicineResponse(
        Long id,
        Long prescriptionId,
        String name,
        int dailyDoseCount,
        String administrationMethod,
        String memo
) {
    public static MedicineResponse from(MedicineResult result) {
        return new MedicineResponse(
                result.id(),
                result.prescriptionId(),
                result.name(),
                result.dailyDoseCount(),
                result.administrationMethod(),
                result.memo()
        );
    }
}
