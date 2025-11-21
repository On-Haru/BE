package com.example.onharu.prescription.application;

import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.prescription.application.dto.PrescriptionCreateCommand;
import com.example.onharu.prescription.application.dto.PrescriptionResult;
import com.example.onharu.prescription.domain.Prescription;
import com.example.onharu.prescription.domain.PrescriptionRepository;
import com.example.onharu.user.domain.User;
import com.example.onharu.user.domain.UserRepository;
import com.example.onharu.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public PrescriptionResult createPrescription(PrescriptionCreateCommand command) {
        User senior = userRepository.findById(command.seniorId())
                .filter(user -> user.getRole() == UserRole.SENIOR)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARE_RECEIVER_NOT_FOUND));
        Prescription prescription = Prescription.create(
                senior,
                command.issuedDate(),
                command.expiredDate(),
                command.doctorName(),
                command.note()
        );
        return PrescriptionResult.from(prescriptionRepository.save(prescription));
    }

    public PrescriptionResult getPrescription(Long prescriptionId) {
        return prescriptionRepository.findById(prescriptionId)
                .map(PrescriptionResult::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRESCRIPTION_NOT_FOUND));
    }
}
