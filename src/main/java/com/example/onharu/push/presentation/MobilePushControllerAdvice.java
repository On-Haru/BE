package com.example.onharu.push.presentation;

import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
import com.example.onharu.global.exception.ErrorCode;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = MobilePushController.class)
public class MobilePushControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("요청 본문이 올바르지 않습니다.");
        return ResponseEntity.badRequest().body(
                ApiResponseFactory.failure(ErrorCode.MOBILE_PUSH_TOKEN_VALIDATION_FAILED, message));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> handleUnreadable(Exception ex) {
        return ResponseEntity.badRequest().body(
                ApiResponseFactory.failure(ErrorCode.MOBILE_PUSH_TOKEN_VALIDATION_FAILED,
                        ex.getMessage()));
    }
}

