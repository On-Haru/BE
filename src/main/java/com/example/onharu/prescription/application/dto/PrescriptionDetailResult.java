package com.example.onharu.prescription.application.dto;

import com.example.onharu.medicineschedule.domain.ScheduleType;
import com.example.onharu.prescription.domain.Prescription;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PrescriptionDetailResult(
        Long id,
        Long seniorId,
        LocalDate issuedDate,
        String hospitalName,
        String doctorName,
        String note,
        List<MedicineDetail> medicines
) {

    public static PrescriptionDetailResult from(Prescription prescription,
            List<MedicineDetail> medicines) {
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

    public record MedicineDetail(
            Long id,
            Long prescriptionId,
            String name,
            int dosage,
            Integer totalCount,
            Integer durationDays,
            String memo,
            String aiDescription,
            List<ScheduleDetail> schedules
    ) {
    }

    public record ScheduleDetail(
            Long id,
            ScheduleType timeTag,
            LocalTime notifyTime
    ) {
    }
}
