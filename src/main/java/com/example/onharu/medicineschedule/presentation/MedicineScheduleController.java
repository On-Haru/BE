package com.example.onharu.medicineschedule.presentation;

import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
import com.example.onharu.medicineschedule.application.MedicineScheduleService;
import com.example.onharu.medicineschedule.presentation.dto.MedicineScheduleCreateRequest;
import com.example.onharu.medicineschedule.presentation.dto.MedicineScheduleResponse;
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
@RequestMapping("/api/medicine-schedules")
@RequiredArgsConstructor
public class MedicineScheduleController {

    private final MedicineScheduleService medicineScheduleService;

    @PostMapping
    public ApiResponse<MedicineScheduleResponse> create(@Valid @RequestBody MedicineScheduleCreateRequest request) {
        var result = medicineScheduleService.createSchedule(request.toCommand());
        return ApiResponseFactory.success(MedicineScheduleResponse.from(result));
    }

    @GetMapping("/{scheduleId}")
    public ApiResponse<MedicineScheduleResponse> get(@PathVariable Long scheduleId) {
        var result = medicineScheduleService.getSchedule(scheduleId);
        return ApiResponseFactory.success(MedicineScheduleResponse.from(result));
    }

    @GetMapping("/medicine/{medicineId}")
    public ApiResponse<List<MedicineScheduleResponse>> getByMedicine(@PathVariable Long medicineId) {
        List<MedicineScheduleResponse> responses = medicineScheduleService.getSchedulesByMedicine(medicineId)
                .stream()
                .map(MedicineScheduleResponse::from)
                .collect(Collectors.toList());
        return ApiResponseFactory.success(responses);
    }
}
