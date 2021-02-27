package com.api.hotifi.speedtest.error;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class SpeedTestErrorCodes extends ErrorCodes {
    //Internal Error Codes
    public static ErrorCode UNEXPECTED_SPEED_TEST_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_SPEED_TEST_ERROR", SpeedTestErrorMessages.UNEXPECTED_SPEED_TEST_ERROR)), 500);
    public static ErrorCode SPEED_TEST_RECORD_NOT_FOUND = new ErrorCode("01", Collections.singletonList(getMessage("SPEED_TEST_RECORD_NOT_FOUND", SpeedTestErrorMessages.SPEED_TEST_RECORD_NOT_FOUND)), 500);
}
