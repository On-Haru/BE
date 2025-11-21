package com.example.onharu.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("US001", HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),
    PHONE_ALREADY_REGISTERED("US002", HttpStatus.CONFLICT, "전화번호가 이미 등록되어 있습니다."),
    CARE_RECEIVER_INFO_MISMATCH("US003", HttpStatus.BAD_REQUEST, "피보호자 정보가 일치하지 않습니다."),
    CARE_RECEIVER_NOT_FOUND("US004", HttpStatus.NOT_FOUND, "피보호자를 찾을 수 없습니다."),
    CAREGIVER_ALREADY_REGISTERED("CG001", HttpStatus.CONFLICT, "이미 등록된 보호자입니다."),
    PUSH_NOTIFICATION_FAILED("PS001", HttpStatus.BAD_REQUEST, "알람을 보내지 못했습니다."),
    PRESCRIPTION_NOT_FOUND("PR001", HttpStatus.NOT_FOUND, "처방전을 찾을 수 없습니다."),
    MEDICINE_NOT_FOUND("MD001", HttpStatus.NOT_FOUND, "약을 찾을 수 없습니다."),
    MEDICINE_SCHEDULE_NOT_FOUND("MS001", HttpStatus.NOT_FOUND, "복약 일정을 찾을 수 없습니다."),
    TAKING_LOG_NOT_FOUND("TL001", HttpStatus.NOT_FOUND, "복약 기록을 찾을 수 없습니다."),
    ALARM_HISTORY_NOT_FOUND("AL001", HttpStatus.NOT_FOUND, "알람 기록을 찾을 수 없습니다."),
    AUTH_TOKEN_REQUIRED("AU001", HttpStatus.UNAUTHORIZED, "인증 토큰이 필요합니다."),
    INVALID_AUTH_TOKEN("AU002", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    PHONE_OR_CODE_MISMATCH("AU003", HttpStatus.BAD_REQUEST, "전화번호 또는 인증 코드가 일치하지 않습니다."),
    INTERNAL_SERVER_ERROR("GL001", HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 서버 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
