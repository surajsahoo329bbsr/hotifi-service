package com.api.hotifi.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotifiExceptionResponse {

    private String error;

    public HotifiExceptionResponse(String error){
        this.error = error;
    }
}
