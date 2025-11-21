package com.example.onharu.prescription.application.dto;

import com.example.onharu.medicineschedule.domain.ScheduleType;
import java.time.LocalDate;
import java.util.List;
import java.time.LocalTime;

public record PrescriptionCreateCommand(
        Long seniorId,
        LocalDate issuedDate,
        String hospitalName,
        String doctorName,
        String note,
        List<MedicineCommand> medicines
) {

    public static PrescriptionCreateCommand of(Long seniorId, LocalDate issuedDate,
            String hospitalName, String doctorName, String note,
            List<MedicineCommand> medicines) {
        return new PrescriptionCreateCommand(seniorId, issuedDate, hospitalName, doctorName,
                note, medicines);
    }

    public record MedicineCommand(
            String name,
            int dosage,
            int totalCount,
            int durationDays,
            String memo,
            String aiDescription,
            List<ScheduleCommand> schedules
    ) {
    }

    public record ScheduleCommand(
            ScheduleType scheduleType,
            LocalTime notifyTime
    ) {
    }
}
