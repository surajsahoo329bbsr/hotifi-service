package com.api.hotifi.common.exception.errors;

import java.util.Collections;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ErrorCodes {

    // 500
    public static final ErrorCode INTERNAL_ERROR = new ErrorCode("01", Collections.singletonList(getMessage("INTERNAL_ERROR", ErrorMessages.INTERNAL_ERROR)), 500);

    // 400
    public static final ErrorCode INVALID_PAYLOAD = new ErrorCode("01", Collections.singletonList(getMessage("INVALID_PAYLOAD", ErrorMessages.INVALID_PAYLOAD)), 400);
    public static final ErrorCode INVALID_HEADER = new ErrorCode("02", Collections.singletonList(getMessage("INVALID_HEADER", ErrorMessages.INVALID_HEADER)), 400);
    public static final ErrorCode UNSUPPORTED_API = new ErrorCode("03", Collections.singletonList(getMessage("UNSUPPORTED_API", ErrorMessages.UNSUPPORTED_API)), 400);
    public static final ErrorCode INVALID_METHOD = new ErrorCode("04", Collections.singletonList(getMessage("INVALID_METHOD", ErrorMessages.UNSUPPORTED_API)), 400);

    // 403
    public static final ErrorCode FAIL_AUTH = new ErrorCode("01", Collections.singletonList(getMessage("FAIL_AUTH", ErrorMessages.FAIL_AUTH)), 403);

    // 401
    public static final ErrorCode FORBIDDEN = new ErrorCode("01", Collections.singletonList(getMessage("FORBIDDEN", ErrorMessages.FORBIDDEN)), 401);

    public static String getLocalizedMessage(String key) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");
        return resourceBundle.getString(key);
    }

    public static String getMessage(String key, String defaultMessage) {
        try {
            return getLocalizedMessage(key);
        } catch (MissingResourceException e) {
            return defaultMessage;
        }
    }
}
