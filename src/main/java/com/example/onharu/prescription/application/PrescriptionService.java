package com.example.onharu.prescription.application;

import com.example.onharu.global.client.OCRClient;
import com.example.onharu.global.client.ai.AiClient;
import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.medicine.domain.Medicine;
import com.example.onharu.medicine.domain.MedicineRepository;
import com.example.onharu.medicineschedule.domain.MedicineScheduleRepository;
import com.example.onharu.ocr.application.ai.OcrAiScenario;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMedicationService prescriptionMedicationService;
    private final MedicineRepository medicineRepository;
    private final MedicineScheduleRepository medicineScheduleRepository;
    private final UserRepository userRepository;
    private final OCRClient ocrClient;
    private final AiClient aiClient;
    private final OcrAiScenario ocrAiScenario;

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
        List<PrescriptionDetailResult.MedicineDetail> medicines = medicineRepository.findByPrescriptionId(
                        prescription.getId())
                .stream()
                .map(this::toMedicineDetail)
                .toList();
        return PrescriptionDetailResult.from(prescription, medicines);
    }

    private PrescriptionDetailResult.MedicineDetail toMedicineDetail(Medicine medicine) {
        List<PrescriptionDetailResult.ScheduleDetail> schedules = medicineScheduleRepository.findByMedicineId(
                        medicine.getId())
                .stream()
                .map(schedule -> new PrescriptionDetailResult.ScheduleDetail(
                        schedule.getId(),
                        schedule.getScheduleType(),
                        schedule.getNotifyTime()
                ))
                .toList();
        return new PrescriptionDetailResult.MedicineDetail(
                medicine.getId(),
                medicine.getPrescription().getId(),
                medicine.getName(),
                medicine.getDailyDoseCount(),
                medicine.getTotalCount(),
                medicine.getDurationDays(),
                medicine.getMemo(),
                medicine.getAiDescription(),
                schedules
        );
    }

    public OcrAiScenario.Response processPrescriptionImage(MultipartFile image) {
        String extractedText = ocrClient.extractText(image);
        //log.info("[OCR] 추출 텍스트: {}", extractedText);
        return aiClient.execute(ocrAiScenario, new OcrAiScenario.Request(extractedText));
    }
}
