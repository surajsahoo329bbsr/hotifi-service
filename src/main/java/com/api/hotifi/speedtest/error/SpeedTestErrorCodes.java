package com.api.hotifi.speedtest.error;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class SpeedTestErrorCodes extends ErrorCodes {
    public static ErrorCode UNEXPECTED_SPEED_TEST_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_SPEED_TEST_ERROR", SpeedTestErrorMessages.UNEXPECTED_SPEED_TEST_ERROR)), 500);
    public static ErrorCode NO_SPEED_TEST_RECORD_EXISTS = new ErrorCode("01", Collections.singletonList(getMessage("NO_SPEED_TEST_RECORD_EXISTS", SpeedTestErrorMessages.NO_SPEED_TEST_EXISTS)), 500);
}
