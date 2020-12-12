package com.api.hotifi.common.exception;

import com.api.hotifi.common.exception.error.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotifiException extends Exception{

    private String message;

    private ErrorCode errorCode;

    public HotifiException(String message, ErrorCode errorCode){
        this.message = message;
        this.errorCode = errorCode;
    }
}
