package com.example.onharu.takinglog.presentation.dto;


import com.example.onharu.takinglog.application.dto.TakenLogCommand;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TakenRequest(
        @NotNull Long scheduleId,
        @NotNull LocalDateTime scheduledDateTime,
        boolean taken
) {

    public TakenLogCommand toCommand() {
        return new TakenLogCommand(scheduleId, scheduledDateTime,
                taken);
    }
}

