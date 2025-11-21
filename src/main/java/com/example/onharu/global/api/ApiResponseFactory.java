package com.example.onharu.global.api;

import com.example.onharu.global.exception.ErrorCode;

public final class ApiResponseFactory {

    private ApiResponseFactory() {
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null, null);
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, errorCode.name(), errorCode.getMessage());
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode, String customMessage) {
        return new ApiResponse<>(false, null, errorCode.name(), customMessage);
    }
}
