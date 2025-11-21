package com.example.onharu.global.api;

public record ApiResponse<T>(boolean success, T data, String errorCode, String message) {
}
