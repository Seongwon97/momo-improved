package com.woowacourse.momo.global.exception.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    NOT_SUPPORTED_URI_ERROR(404, "NOT_SUPPORTED_URI", "지원하지 않는 URI 요청입니다."),
    NOT_SUPPORTED_METHOD_ERROR(405, "NOT_SUPPORTED_METHOD", "지원하지 않는 Method 요청입니다."),

    MAX_UPLOAD_SIZE_FILE_ERROR(400, "MAX_UPLOAD_SIZE_FILE", "요청한 파일의 크기가 제한된 크기보다 큽니다."),

    LOCK_ACQUISITION_FAILED_ERROR(400, "LOCK_ACQUISITION_FAILED_ERROR", "Lock 획득을 실패했습니다."),
    LOCK_INTERRUPTED_ERROR(400, "LOCK_INTERRUPTED_ERROR", "Lock 획득 과정에서 오류가 발생했습니다."),

    VALIDATION_ERROR(400, "VALIDATION_001", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(500, "SERVER_001", "내부 서버 오류입니다."),
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;
}
