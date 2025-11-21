package com.example.onharu.prescription.presentation.dto;

import com.example.onharu.medicineschedule.domain.ScheduleType;
import com.example.onharu.prescription.application.dto.PrescriptionCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public record PrescriptionRequest(
        @NotNull(message = "시니어 ID는 필수입니다.")
        Long seniorId,

        @NotBlank(message = "병원명은 필수입니다.")
        String hospitalName,

        @NotBlank(message = "의사명은 필수입니다.")
        String doctorName,

        @NotNull(message = "처방 일자는 필수입니다.")
        LocalDate issuedDate,

        String note,

        @NotNull(message = "처방 약 목록은 필수입니다.")
        List<MedicineDto> medicines
) {

    public static PrescriptionCreateCommand toCommand(PrescriptionRequest request) {
        return PrescriptionCreateCommand.of(
                request.seniorId,
                request.issuedDate,
                request.hospitalName,
                request.doctorName,
                request.note,
                request.medicines.stream()
                        .map(MedicineDto::toCommand)
                        .collect(Collectors.toList())
        );
    }

    public record MedicineDto(
            @NotBlank(message = "약 이름은 필수입니다.")
            String name,

            int dosage,
            int totalCount,
            int durationDays,
            String memo,
            String aiDescription,

            @NotNull(message = "복용 스케줄은 필수입니다.")
            List<ScheduleDto> schedules
    ) {
        private PrescriptionCreateCommand.MedicineCommand toCommand() {
            return new PrescriptionCreateCommand.MedicineCommand(
                    name,
                    dosage,
                    totalCount,
                    durationDays,
                    memo,
                    aiDescription,
                    schedules.stream()
                            .map(ScheduleDto::toCommand)
                            .collect(Collectors.toList())
            );
        }
    }

    public record ScheduleDto(
            @NotBlank(message = "알림 시각은 필수입니다.")
            String notifyTime,

            @NotBlank(message = "시간대 태그는 필수입니다.")
            String timeTag
    ) {
        private PrescriptionCreateCommand.ScheduleCommand toCommand() {
            return new PrescriptionCreateCommand.ScheduleCommand(
                    ScheduleType.valueOf(timeTag.toUpperCase(Locale.ROOT)),
                    LocalTime.parse(notifyTime)
            );
        }
    }
}
