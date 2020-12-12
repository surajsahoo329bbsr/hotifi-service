package com.api.hotifi.common.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FieldErrorDetail {

    private String field;
    private String message;

    private FieldErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }
}

