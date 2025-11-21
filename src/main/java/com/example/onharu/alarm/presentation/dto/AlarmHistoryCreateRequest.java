package com.example.onharu.alarm.presentation.dto;

import com.example.onharu.alarm.application.dto.AlarmHistoryCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AlarmHistoryCreateRequest(
        @NotNull Long scheduleId,
        @NotBlank String channel,
        @NotNull LocalDateTime sentAt,
        boolean success,
        String failureReason
) {
    public AlarmHistoryCreateCommand toCommand() {
        return AlarmHistoryCreateCommand.of(scheduleId, channel, sentAt, success, failureReason);
    }
}
