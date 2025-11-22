package com.example.onharu.takinglog.application.dto;

import java.time.YearMonth;

public record TakingLogMonthlyRequest(
        Long userId,
        int year,
        int month
) {

    public static TakingLogMonthlyRequest of(Long userId, int year, int month) {
        return new TakingLogMonthlyRequest(userId, year, month);
    }

    public YearMonth toYearMonth() {
        return YearMonth.of(year, month);
    }
}
