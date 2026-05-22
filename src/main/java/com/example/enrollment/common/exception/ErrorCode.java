package com.example.enrollment.common.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Class
    CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, "강의를 찾을 수 없습니다."),
    CLASS_NOT_OPEN(HttpStatus.BAD_REQUEST, "모집 중인 강의가 아닙니다."),
    CLASS_INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "잘못된 상태 전이입니다."),

    // Enrollment
    ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "수강 신청을 찾을 수 없습니다."),
    ENROLLMENT_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "정원이 초과되었습니다."),
    ENROLLMENT_DUPLICATE(HttpStatus.CONFLICT, "이미 신청한 강의입니다."),
    ENROLLMENT_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "이미 취소된 신청입니다."),
    ENROLLMENT_CANCEL_PERIOD_EXPIRED(HttpStatus.BAD_REQUEST, "취소 가능 기간(7일)이 초과되었습니다."),
    ENROLLMENT_INVALID_STATUS(HttpStatus.BAD_REQUEST, "현재 상태에서는 불가능한 작업입니다."),

    //Authorization
    NOT_CLASS_OWNER(HttpStatus.FORBIDDEN, "강의 개설자만 접근할 수 있습니다."),
    NOT_ENROLLMENT_OWNER(HttpStatus.FORBIDDEN, "본인 신청만 처리할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
