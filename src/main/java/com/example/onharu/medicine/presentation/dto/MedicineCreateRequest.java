package com.example.onharu.medicine.presentation.dto;

import com.example.onharu.medicine.application.dto.MedicineCreateCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MedicineCreateRequest(
        @NotNull Long prescriptionId,
        @NotBlank String name,
        @Min(1) int dailyDoseCount,
        @NotBlank String administrationMethod,
        String memo
) {
    public MedicineCreateCommand toCommand() {
        return MedicineCreateCommand.of(prescriptionId, name, dailyDoseCount, administrationMethod, memo);
    }
}
