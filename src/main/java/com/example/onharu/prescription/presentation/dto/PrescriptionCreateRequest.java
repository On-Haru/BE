package com.example.onharu.prescription.presentation.dto;

import com.example.onharu.prescription.application.dto.PrescriptionCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PrescriptionCreateRequest(
        @NotNull Long seniorId,
        @NotNull LocalDate issuedDate,
        LocalDate expiredDate,
        @NotBlank String doctorName,
        String note
) {
    public PrescriptionCreateCommand toCommand() {
        return PrescriptionCreateCommand.of(seniorId, issuedDate, expiredDate, doctorName, note);
    }
}
