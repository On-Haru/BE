package com.example.onharu.prescription.presentation.dto;

import com.example.onharu.ocr.application.ai.OcrAiScenario;
import com.example.onharu.prescription.application.dto.PrescriptionDetailResult;
import com.example.onharu.prescription.application.dto.PrescriptionResult;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

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

    public static PrescriptionResponse fromOcrDraft(OcrAiScenario.Response response) {
        List<MedicineItem> medicines = response.medicines() == null
                ? List.of()
                : response.medicines().stream()
                        .map(medicine -> new MedicineItem(
                                null,
                                null,
                                medicine.name(),
                                safeInt(medicine.dosage()),
                                medicine.totalCount(),
                                medicine.durationDays(),
                                medicine.memo(),
                                medicine.aiDescription(),
                                medicine.schedules() == null
                                        ? List.of()
                                        : medicine.schedules().stream()
                                                .map(schedule -> new ScheduleItem(
                                                        null,
                                                        schedule.notifyTime(),
                                                        normalizeTimeTag(schedule.timeTag())
                                                ))
                                                .toList()
                        ))
                        .toList();

        return new PrescriptionResponse(
                null,
                response.seniorId(),
                response.hospitalName(),
                response.doctorName(),
                parseIssuedDate(response.issuedDate()),
                response.note(),
                medicines
        );
    }

    private static LocalDate parseIssuedDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ignored) {
            try {
                DateTimeFormatter alt = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                return LocalDate.parse(value, alt);
            } catch (DateTimeParseException ex) {
                return null;
            }
        }
    }

    private static int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private static String normalizeTimeTag(String value) {
        return value == null ? null : value.toUpperCase(Locale.ROOT);
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
