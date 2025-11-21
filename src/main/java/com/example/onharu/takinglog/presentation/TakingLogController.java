package com.example.onharu.takinglog.presentation;

import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
import com.example.onharu.takinglog.application.TakingLogService;
import com.example.onharu.takinglog.presentation.dto.TakingLogCreateRequest;
import com.example.onharu.takinglog.presentation.dto.TakingLogResponse;
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
@RequestMapping("/api/taking-logs")
@RequiredArgsConstructor
public class TakingLogController {

    private final TakingLogService takingLogService;

    @PostMapping
    public ApiResponse<TakingLogResponse> create(@Valid @RequestBody TakingLogCreateRequest request) {
        var result = takingLogService.recordTaking(request.toCommand());
        return ApiResponseFactory.success(TakingLogResponse.from(result));
    }

    @GetMapping("/{logId}")
    public ApiResponse<TakingLogResponse> get(@PathVariable Long logId) {
        var result = takingLogService.getLog(logId);
        return ApiResponseFactory.success(TakingLogResponse.from(result));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ApiResponse<List<TakingLogResponse>> getBySchedule(@PathVariable Long scheduleId) {
        List<TakingLogResponse> responses = takingLogService.getLogsBySchedule(scheduleId)
                .stream()
                .map(TakingLogResponse::from)
                .collect(Collectors.toList());
        return ApiResponseFactory.success(responses);
    }
}
