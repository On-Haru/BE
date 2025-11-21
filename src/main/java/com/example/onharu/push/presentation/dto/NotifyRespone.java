package com.example.onharu.push.presentation.dto;

public record NotifyRespone(Long scheduleId, String title, String body) {

    public static NotifyRespone of(Long scheduleId, String title, String body) {
        return new NotifyRespone(scheduleId, title, body);
    }
}
