package com.api.hotifi.common.exception.error;

import java.util.List;

public class ErrorResponse {

    private String code;
    private List<String> messages;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getErrorCode();
        this.messages = errorCode.getMessages();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

}
