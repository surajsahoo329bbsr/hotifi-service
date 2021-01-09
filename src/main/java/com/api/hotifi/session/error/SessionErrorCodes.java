package com.api.hotifi.session.error;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class SessionErrorCodes extends ErrorCodes {
    public static ErrorCode UNEXPECTED_SESSION_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_SESSION_ERROR", SessionErrorMessages.UNEXPECTED_SESSION_ERROR)), 400);
    public static ErrorCode SELLER_NOT_LEGIT = new ErrorCode("01", Collections.singletonList(getMessage("SELLER_NOT_LEGIT", SessionErrorMessages.SELLER_NOT_LEGIT)), 500);
    public static ErrorCode WIFI_SPEED_TEST_ABSENT = new ErrorCode("02", Collections.singletonList(getMessage("WIFI_SPEED_TEST_ABSENT", SessionErrorMessages.WIFI_SPEED_TEST_ABSENT)), 500);
    public static ErrorCode SPEED_TEST_ABSENT = new ErrorCode("03", Collections.singletonList(getMessage("SPEED_TEST_ABSENT", SessionErrorMessages.SPEED_TEST_ABSENT)), 500);
    public static ErrorCode WITHDRAW_SELLER_AMOUNT = new ErrorCode("04", Collections.singletonList(getMessage("WITHDRAW_SELLER_AMOUNT", SessionErrorMessages.WITHDRAW_SELLER_AMOUNT)), 500);
}
