package com.example.onharu.prescription.application;

import com.example.onharu.medicine.domain.Medicine;
import com.example.onharu.medicine.domain.MedicineRepository;
import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.MedicineScheduleRepository;
import com.example.onharu.medicineschedule.domain.RepeatType;
import com.example.onharu.prescription.application.dto.PrescriptionCreateCommand;
import com.example.onharu.prescription.application.dto.PrescriptionCreateCommand.MedicineCommand;
import com.example.onharu.prescription.application.dto.PrescriptionCreateCommand.ScheduleCommand;
import com.example.onharu.prescription.domain.Prescription;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class PrescriptionMedicationService {

    private static final int EVERYDAY_BITMASK = 0b1111111;

    private final MedicineRepository medicineRepository;
    private final MedicineScheduleRepository medicineScheduleRepository;

    public void registerMedicines(Prescription prescription, PrescriptionCreateCommand command) {
        for (MedicineCommand medicineCommand : command.medicines()) {
            Medicine medicine = medicineRepository.save(
                    Medicine.createWithDetails(
                            prescription,
                            medicineCommand.name(),
                            medicineCommand.dosage(),
                            null,
                            medicineCommand.memo(),
                            medicineCommand.totalCount(),
                            medicineCommand.durationDays(),
                            medicineCommand.aiDescription()
                    )
            );

            LocalDate startDate = command.issuedDate();
            LocalDate endDate = calculateEndDate(startDate, medicineCommand.durationDays());

            for (ScheduleCommand scheduleCommand : medicineCommand.schedules()) {
                MedicineSchedule schedule = MedicineSchedule.create(
                        medicine,
                        scheduleCommand.scheduleType(),
                        RepeatType.DAILY,
                        EVERYDAY_BITMASK,
                        scheduleCommand.notifyTime(),
                        startDate,
                        endDate
                );
                medicineScheduleRepository.save(schedule);
            }
        }
    }

    private LocalDate calculateEndDate(LocalDate startDate, int durationDays) {
        int safeDuration = Math.max(durationDays, 1);
        return startDate.plusDays(safeDuration - 1L);
    }
}
