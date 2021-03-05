package com.api.hotifi.common.exception;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HotifiException extends RuntimeException {

    private final ErrorCode errorCode;
    private String errorMessage;

    public HotifiException(ErrorCode errorCode, Throwable throwable) {
        super(String.join(",", errorCode.getMessages()), throwable);
        this.errorCode = errorCode;
    }

    public HotifiException(ErrorCode errorCode) {
        super(String.join(",", errorCode.getMessages()));
        this.errorCode = errorCode;
    }

    public static HotifiException wrap(Throwable ex) {
        log.error("Error:", ex);
        return ex instanceof HotifiException ? (HotifiException) ex : new HotifiException(ErrorCodes.INTERNAL_ERROR);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
