package com.api.hotifi.identity.errors;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class DeviceErrorCodes extends ErrorCodes {
    public static ErrorCode DEVICE_ALREADY_ADDED = new ErrorCode("01", Collections.singletonList(getMessage("DEVICE_ALREADY_ADDED", DeviceErrorMessages.DEVICE_ALREADY_ADDED)), 500);
    public static ErrorCode ANDROID_ID_NOT_FOUND = new ErrorCode("03", Collections.singletonList(getMessage("ANDROID_ID_NOT_FOUND", DeviceErrorMessages.ANDROID_ID_NOT_FOUND)), 500);
    public static ErrorCode UNEXPECTED_DEVICE_ERROR = new ErrorCode("03", Collections.singletonList(getMessage("UNEXPECTED_DEVICE_ERROR", DeviceErrorMessages.UNEXPECTED_DEVICE_ERROR)), 500);

    public static ErrorCode FORBIDDEN_ANDROID_DEVICE_ID = new ErrorCode("01", Collections.singletonList(getMessage("FORBIDDEN_ANDROID_DEVICE_ID", DeviceErrorMessages.UNEXPECTED_DEVICE_ERROR)), 400);
}
