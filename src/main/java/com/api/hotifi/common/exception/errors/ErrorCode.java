package com.api.hotifi.common.exception.errors;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;


public class ErrorCode {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    Date timestamp;
    public static final String INPUT_INVALID_CODE = "00";
    private String code;
    private List<String> messages;
    private int httpStatusCode = Integer.MIN_VALUE;

    ErrorCode(){
        timestamp = new Date(System.currentTimeMillis());
    }

    public ErrorCode(String code, List<String> messages, int httpStatusCode) {
        this();
        this.code = code;
        this.messages = messages;
        this.httpStatusCode = httpStatusCode;
    }

    public ErrorCode(String code, List<String> messages) {
        this();
        this.code = code;
        this.messages = messages;
    }

    public String getErrorCode() {
        return this.httpStatusCode != Integer.MIN_VALUE
                ? String.format("%s.%s", this.httpStatusCode, this.code)
                : this.code;
    }

    public String getCode() {
        return code;
    }

    public List<String> getMessages() {
        return messages;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
