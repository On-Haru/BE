package com.example.onharu.push.presentation.dto;

import java.time.LocalDateTime;

public record NotifyRequest(String title, String body) {

    public record NotifyData(Long scheduleId, String title, String body,
                             LocalDateTime scheduledDateTime) {

        public static NotifyData of(Long scheduleId, String title, String body,
                LocalDateTime scheduledDateTime) {
            return new NotifyData(scheduleId, title, body, scheduledDateTime);
        }
    }
}

