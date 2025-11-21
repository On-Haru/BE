package com.example.onharu.medicine.application.dto;

import com.example.onharu.medicine.domain.Medicine;

public record MedicineResult(
        Long id,
        Long prescriptionId,
        String name,
        int dailyDoseCount,
        String administrationMethod,
        String memo,
        Integer totalCount,
        Integer durationDays,
        String aiDescription
) {
    public static MedicineResult from(Medicine medicine) {
        return new MedicineResult(
                medicine.getId(),
                medicine.getPrescription().getId(),
                medicine.getName(),
                medicine.getDailyDoseCount(),
                medicine.getAdministrationMethod(),
                medicine.getMemo(),
                medicine.getTotalCount(),
                medicine.getDurationDays(),
                medicine.getAiDescription()
        );
    }
}
