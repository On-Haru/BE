package com.example.onharu.prescription.application;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.medicine.application.dto.MedicineResult;
import com.example.onharu.medicine.domain.MedicineRepository;
import com.example.onharu.prescription.application.dto.PrescriptionCreateCommand;
import com.example.onharu.prescription.application.dto.PrescriptionDetailResult;
import com.example.onharu.prescription.application.dto.PrescriptionResult;
import com.example.onharu.prescription.domain.Prescription;
import com.example.onharu.prescription.domain.PrescriptionRepository;
import com.example.onharu.user.domain.User;
import com.example.onharu.user.domain.UserRepository;
import com.example.onharu.user.domain.UserRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMedicationService prescriptionMedicationService;
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    @Transactional
    public PrescriptionResult createPrescription(PrescriptionCreateCommand command) {
        User senior = userRepository.findById(command.seniorId())
                .filter(user -> user.getRole() == UserRole.SENIOR)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARE_RECEIVER_NOT_FOUND));

        Prescription prescription = Prescription.create(
                senior,
                command.issuedDate(),
                command.hospitalName(),
                command.doctorName(),
                command.note()
        );
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        prescriptionMedicationService.registerMedicines(savedPrescription, command);
        return PrescriptionResult.from(savedPrescription);
    }

    public PrescriptionDetailResult getPrescription(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRESCRIPTION_NOT_FOUND));
        return buildDetail(prescription);
    }

    public List<PrescriptionDetailResult> getPrescriptionHistory(Long seniorId) {
        User senior = userRepository.findById(seniorId)
                .filter(user -> user.getRole() == UserRole.SENIOR)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARE_RECEIVER_NOT_FOUND));

        return prescriptionRepository.findAllBySeniorIdOrderByIssuedDateDesc(senior.getId())
                .stream()
                .map(this::buildDetail)
                .toList();
    }

    private PrescriptionDetailResult buildDetail(Prescription prescription) {
        List<MedicineResult> medicines = medicineRepository.findByPrescriptionId(prescription.getId())
                .stream()
                .map(MedicineResult::from)
                .toList();
        return PrescriptionDetailResult.from(prescription, medicines);
    }
}
