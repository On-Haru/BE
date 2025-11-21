package com.example.onharu.medicine.application;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.medicine.application.dto.MedicineCreateCommand;
import com.example.onharu.medicine.application.dto.MedicineResult;
import com.example.onharu.medicine.domain.Medicine;
import com.example.onharu.medicine.domain.MedicineRepository;
import com.example.onharu.prescription.domain.Prescription;
import com.example.onharu.prescription.domain.PrescriptionRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Transactional
    public MedicineResult addMedicine(MedicineCreateCommand command) {
        Prescription prescription = prescriptionRepository.findById(command.prescriptionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRESCRIPTION_NOT_FOUND));
        Medicine medicine = Medicine.create(
                prescription,
                command.name(),
                command.dailyDoseCount(),
                command.administrationMethod(),
                command.memo()
        );
        return MedicineResult.from(medicineRepository.save(medicine));
    }

    public MedicineResult getMedicine(Long medicineId) {
        return medicineRepository.findById(medicineId)
                .map(MedicineResult::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICINE_NOT_FOUND));
    }

    public List<MedicineResult> getMedicinesByPrescription(Long prescriptionId) {
        return medicineRepository.findByPrescriptionId(prescriptionId)
                .stream()
                .map(MedicineResult::from)
                .collect(Collectors.toList());
    }
}
