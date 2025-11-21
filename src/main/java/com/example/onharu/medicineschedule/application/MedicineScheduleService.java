package com.example.onharu.medicineschedule.application;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.medicine.domain.Medicine;
import com.example.onharu.medicine.domain.MedicineRepository;
import com.example.onharu.medicineschedule.application.dto.MedicineScheduleCreateCommand;
import com.example.onharu.medicineschedule.application.dto.MedicineScheduleResult;
import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import com.example.onharu.medicineschedule.domain.MedicineScheduleRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicineScheduleService {

    private final MedicineScheduleRepository medicineScheduleRepository;
    private final MedicineRepository medicineRepository;

    @Transactional
    public MedicineScheduleResult createSchedule(MedicineScheduleCreateCommand command) {
        Medicine medicine = medicineRepository.findById(command.medicineId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICINE_NOT_FOUND));

        MedicineSchedule schedule = MedicineSchedule.create(
                medicine,
                command.scheduleType(),
                command.repeatType(),
                command.daysBitmask(),
                command.notifyTime(),
                command.startDate(),
                command.endDate()
        );
        return MedicineScheduleResult.from(medicineScheduleRepository.save(schedule));
    }

    public MedicineScheduleResult getSchedule(Long scheduleId) {
        return medicineScheduleRepository.findById(scheduleId)
                .map(MedicineScheduleResult::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICINE_SCHEDULE_NOT_FOUND));
    }

    public List<MedicineScheduleResult> getSchedulesByMedicine(Long medicineId) {
        return medicineScheduleRepository.findByMedicineId(medicineId)
                .stream()
                .map(MedicineScheduleResult::from)
                .collect(Collectors.toList());
    }
}
