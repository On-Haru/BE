package com.example.onharu.prescription.presentation.dto;

import com.example.onharu.prescription.application.dto.PrescriptionDetailResult;
import com.example.onharu.prescription.application.dto.PrescriptionResult;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record PrescriptionResponse(
        Long id,
        Long seniorId,
        String hospitalName,
        String doctorName,
        LocalDate issuedDate,
        String note,
        List<MedicineItem> medicines
) {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static PrescriptionResponse from(PrescriptionResult result) {
        return new PrescriptionResponse(
                result.id(),
                result.seniorId(),
                result.hospitalName(),
                result.doctorName(),
                result.issuedDate(),
                result.note(),
                List.of()
        );
    }

    public static PrescriptionResponse fromDetail(PrescriptionDetailResult result) {
        List<MedicineItem> medicines = result.medicines().stream()
                .map(medicine -> new MedicineItem(
                        medicine.id(),
                        medicine.prescriptionId(),
                        medicine.name(),
                        medicine.dosage(),
                        medicine.totalCount(),
                        medicine.durationDays(),
                        medicine.memo(),
                        medicine.aiDescription(),
                        medicine.schedules().stream()
                                .map(schedule -> new ScheduleItem(
                                        schedule.id(),
                                        schedule.notifyTime().format(TIME_FORMATTER),
                                        schedule.timeTag().name()
                                ))
                                .toList()
                ))
                .toList();

        return new PrescriptionResponse(
                result.id(),
                result.seniorId(),
                result.hospitalName(),
                result.doctorName(),
                result.issuedDate(),
                result.note(),
                medicines
        );
    }

    public record MedicineItem(
            Long id,
            Long prescriptionId,
            String name,
            int dosage,
            Integer totalCount,
            Integer durationDays,
            String memo,
            String aiDescription,
            List<ScheduleItem> schedules
    ) {
    }

    public record ScheduleItem(
            Long id,
            String notifyTime,
            String timeTag
    ) {
    }
}
