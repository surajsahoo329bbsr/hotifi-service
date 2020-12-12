package com.api.hotifi.identity.error;

import com.api.hotifi.common.exception.error.ErrorCode;
import com.api.hotifi.common.exception.error.ErrorCodes;

import java.util.Collections;

public class UserErrorCodes extends ErrorCodes {
    public static final ErrorCode USER_EXISTS = new ErrorCode("01", Collections.singletonList(getMessage("USER_EXISTS", UserErrorMessages.USER_EXISTS)), 400);
    public static final ErrorCode INVALID_TOKEN = new ErrorCode("02", Collections.singletonList(getMessage("INVALID_TOKEN", UserErrorMessages.INVALID_TOKEN)), 400);
    public static final ErrorCode INVALID_OTP = new ErrorCode("03", Collections.singletonList(getMessage("INVALID_OTP", UserErrorMessages.INVALID_OTP)), 400);
    public static final ErrorCode TOKEN_EXPIRED = new ErrorCode("04", Collections.singletonList(getMessage("TOKEN_EXPIRED", UserErrorMessages.TOKEN_EXPIRED)), 400);
    public static final ErrorCode USER_ALREADY_VERIFIED = new ErrorCode("05", Collections.singletonList(getMessage("USER_ALREADY_VERIFIED", UserErrorMessages.USER_ALREADY_VERIFIED)), 400);
    public static final ErrorCode USER_ALREADY_ACTIVATED = new ErrorCode("06", Collections.singletonList(getMessage("USER_ALREADY_ACTIVATED", UserErrorMessages.USER_ALREADY_ACTIVATED)), 400);
    public static final ErrorCode USER_ALREADY_FREEZED = new ErrorCode("07", Collections.singletonList(getMessage("USER_ALREADY_FREEZED", UserErrorMessages.USER_ALREADY_FREEZED)), 400);
    public static final ErrorCode USER_ALREADY_BANNED = new ErrorCode("08", Collections.singletonList(getMessage("USER_ALREADY_BANNED", UserErrorMessages.USER_ALREADY_BANNED)), 400);
    public static final ErrorCode USER_ALREADY_DELETED = new ErrorCode("09", Collections.singletonList(getMessage("USER_ALREADY_DELETED", UserErrorMessages.USER_ALREADY_DELETED)), 400);
}
