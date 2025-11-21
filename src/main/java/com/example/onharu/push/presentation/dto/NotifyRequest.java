package com.example.onharu.push.presentation.dto;

import java.time.LocalDateTime;

public record NotifyRequest(Long scheduleId, String title, String body) {

    public static NotifyData toNotifyData(NotifyRequest request) {
        return new NotifyData(request.scheduleId(), request.title(), request.body(),
                LocalDateTime.now());
    }

    public record NotifyData(Long scheduleId, String title, String body,
                             LocalDateTime scheduledDateTime) {

        public static NotifyData of(Long scheduleId, String title, String body,
                LocalDateTime scheduledDateTime) {
            return new NotifyData(scheduleId, title, body, scheduledDateTime);
        }
    }
}

