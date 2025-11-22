package com.example.onharu.report.presentation;

import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
import com.example.onharu.report.application.ReportService;
import com.example.onharu.report.application.dto.ReportPayload;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ApiResponse<ReportPayload> getCareReport(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        YearMonth target = (year != null && month != null)
                ? YearMonth.of(year, month)
                : YearMonth.now();

        return ApiResponseFactory.success(
                reportService.getReport(userId, target).orElse(null)
        );
    }
}
