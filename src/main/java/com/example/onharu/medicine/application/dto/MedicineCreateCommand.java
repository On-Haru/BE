package com.example.onharu.medicine.application.dto;

public record MedicineCreateCommand(
        Long prescriptionId,
        String name,
        int dailyDoseCount,
        String administrationMethod,
        String memo
) {
    public static MedicineCreateCommand of(Long prescriptionId, String name, int dailyDoseCount, String administrationMethod, String memo) {
        return new MedicineCreateCommand(prescriptionId, name, dailyDoseCount, administrationMethod, memo);
    }
}
