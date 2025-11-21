package com.example.onharu.medicine.presentation;

import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
import com.example.onharu.medicine.application.MedicineService;
import com.example.onharu.medicine.application.dto.MedicineResult;
import com.example.onharu.medicine.presentation.dto.MedicineCreateRequest;
import com.example.onharu.medicine.presentation.dto.MedicineResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping
    public ApiResponse<MedicineResponse> create(@Valid @RequestBody MedicineCreateRequest request) {
        MedicineResult result = medicineService.addMedicine(request.toCommand());
        return ApiResponseFactory.success(MedicineResponse.from(result));
    }

    @GetMapping("/{medicineId}")
    public ApiResponse<MedicineResponse> get(@PathVariable Long medicineId) {
        MedicineResult result = medicineService.getMedicine(medicineId);
        return ApiResponseFactory.success(MedicineResponse.from(result));
    }

    @GetMapping("/prescription/{prescriptionId}")
    public ApiResponse<List<MedicineResponse>> getByPrescription(@PathVariable Long prescriptionId) {
        List<MedicineResponse> responses = medicineService.getMedicinesByPrescription(prescriptionId)
                .stream()
                .map(MedicineResponse::from)
                .collect(Collectors.toList());
        return ApiResponseFactory.success(responses);
    }
}
