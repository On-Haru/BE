package com.example.onharu.prescription.presentation;

import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
import com.example.onharu.prescription.application.PrescriptionService;
import com.example.onharu.prescription.application.dto.PrescriptionDetailResult;
import com.example.onharu.prescription.application.dto.PrescriptionResult;
import com.example.onharu.prescription.presentation.dto.PrescriptionRequest;
import com.example.onharu.prescription.presentation.dto.PrescriptionResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping()
    public ApiResponse<PrescriptionResponse> create(
            @Valid @RequestBody PrescriptionRequest request) {
        PrescriptionResult result = prescriptionService.createPrescription(
                PrescriptionRequest.toCommand(request));
        return ApiResponseFactory.success(PrescriptionResponse.from(result));
    }
//
//    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
//    public ApiResponse<?> upload(@RequestPart("image") MultipartFile image) {
//        prescriptionService.processPrescriptionImage(image);
//    }

    @GetMapping("/{prescriptionId}")
    public ApiResponse<PrescriptionResponse> get(@PathVariable Long prescriptionId) {
        PrescriptionDetailResult result = prescriptionService.getPrescription(prescriptionId);
        return ApiResponseFactory.success(PrescriptionResponse.fromDetail(result));
    }

    @GetMapping
    public ApiResponse<List<PrescriptionResponse>> getHistory(@RequestParam Long seniorId) {
        List<PrescriptionResponse> responses = prescriptionService.getPrescriptionHistory(seniorId)
                .stream()
                .map(PrescriptionResponse::fromDetail)
                .toList();
        return ApiResponseFactory.success(responses);
    }
}
