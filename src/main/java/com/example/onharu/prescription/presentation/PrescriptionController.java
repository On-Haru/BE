package com.example.onharu.prescription.presentation;

import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
import com.example.onharu.prescription.application.PrescriptionService;
import com.example.onharu.prescription.application.dto.PrescriptionResult;
import com.example.onharu.prescription.presentation.dto.PrescriptionCreateRequest;
import com.example.onharu.prescription.presentation.dto.PrescriptionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ApiResponse<PrescriptionResponse> create(@Valid @RequestBody PrescriptionCreateRequest request) {
        PrescriptionResult result = prescriptionService.createPrescription(request.toCommand());
        return ApiResponseFactory.success(PrescriptionResponse.from(result));
    }

    @GetMapping("/{prescriptionId}")
    public ApiResponse<PrescriptionResponse> get(@PathVariable Long prescriptionId) {
        PrescriptionResult result = prescriptionService.getPrescription(prescriptionId);
        return ApiResponseFactory.success(PrescriptionResponse.from(result));
    }
}
