package com.example.onharu.alarm.presentation;

import com.example.onharu.alarm.application.AlarmHistoryService;
import com.example.onharu.alarm.presentation.dto.AlarmHistoryCreateRequest;
import com.example.onharu.alarm.presentation.dto.AlarmHistoryResponse;
import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
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
@RequestMapping("/api/alarm-histories")
@RequiredArgsConstructor
public class AlarmHistoryController {

    private final AlarmHistoryService alarmHistoryService;

    @PostMapping
    public ApiResponse<AlarmHistoryResponse> record(@Valid @RequestBody AlarmHistoryCreateRequest request) {
        var result = alarmHistoryService.recordHistory(request.toCommand());
        return ApiResponseFactory.success(AlarmHistoryResponse.from(result));
    }

    @GetMapping("/{historyId}")
    public ApiResponse<AlarmHistoryResponse> get(@PathVariable Long historyId) {
        var result = alarmHistoryService.getHistory(historyId);
        return ApiResponseFactory.success(AlarmHistoryResponse.from(result));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ApiResponse<List<AlarmHistoryResponse>> getBySchedule(@PathVariable Long scheduleId) {
        List<AlarmHistoryResponse> responses = alarmHistoryService.getHistoriesBySchedule(scheduleId)
                .stream()
                .map(AlarmHistoryResponse::from)
                .collect(Collectors.toList());
        return ApiResponseFactory.success(responses);
    }
}
