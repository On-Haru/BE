package com.example.onharu.takinglog.presentation.dto;

import com.example.onharu.takinglog.application.dto.TakingLogCreateCommand;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TakingLogCreateRequest(
        @NotNull Long scheduleId,
        @NotNull LocalDateTime scheduledDateTime,
        LocalDateTime takenDateTime,
        boolean taken,
        Integer delayMinutes
) {
    public TakingLogCreateCommand toCommand() {
        return TakingLogCreateCommand.of(scheduleId, scheduledDateTime, takenDateTime, taken, delayMinutes);
    }
}
