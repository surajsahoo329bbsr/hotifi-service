package com.api.hotifi.identity.errors;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class UserErrorCodes extends ErrorCodes {
    public static final ErrorCode USER_EXISTS = new ErrorCode("01", Collections.singletonList(getMessage("USER_EXISTS", UserErrorMessages.USER_EXISTS)), 400);
    public static final ErrorCode INVALID_TOKEN = new ErrorCode("02", Collections.singletonList(getMessage("INVALID_TOKEN", UserErrorMessages.INVALID_TOKEN)), 400);
    public static final ErrorCode TOKEN_EXPIRED = new ErrorCode("04", Collections.singletonList(getMessage("TOKEN_EXPIRED", UserErrorMessages.TOKEN_EXPIRED)), 400);
    public static final ErrorCode USER_ALREADY_VERIFIED = new ErrorCode("05", Collections.singletonList(getMessage("USER_ALREADY_VERIFIED", UserErrorMessages.USER_ALREADY_VERIFIED)), 400);
    public static final ErrorCode USER_ALREADY_LOGGED_IN = new ErrorCode("06", Collections.singletonList(getMessage("USER_ALREADY_LOGGED_IN", UserErrorMessages.USER_ALREADY_LOGGED_IN)), 400);
    public static final ErrorCode USER_ALREADY_LOGGED_OUT = new ErrorCode("07", Collections.singletonList(getMessage("USER_ALREADY_LOGGED_OUT", UserErrorMessages.USER_ALREADY_LOGGED_OUT)), 400);
    public static final ErrorCode USER_ALREADY_ACTIVATED = new ErrorCode("08", Collections.singletonList(getMessage("USER_ALREADY_ACTIVATED", UserErrorMessages.USER_ALREADY_ACTIVATED)), 400);
    public static final ErrorCode USER_ALREADY_FREEZED = new ErrorCode("09", Collections.singletonList(getMessage("USER_ALREADY_FREEZED", UserErrorMessages.USER_ALREADY_FREEZED)), 400);
    public static final ErrorCode USER_ALREADY_NOT_FREEZED = new ErrorCode("10", Collections.singletonList(getMessage("USER_ALREADY_NOT_FREEZED", UserErrorMessages.USER_ALREADY_NOT_FREEZED)), 400);
    public static final ErrorCode USER_ALREADY_BANNED = new ErrorCode("11", Collections.singletonList(getMessage("USER_ALREADY_BANNED", UserErrorMessages.USER_ALREADY_BANNED)), 400);
    public static final ErrorCode USER_ALREADY_NOT_BANNED = new ErrorCode("12", Collections.singletonList(getMessage("USER_ALREADY_NOT_BANNED", UserErrorMessages.USER_ALREADY_NOT_BANNED)), 400);
    public static final ErrorCode USER_ALREADY_DELETED = new ErrorCode("12", Collections.singletonList(getMessage("USER_ALREADY_DELETED", UserErrorMessages.USER_ALREADY_DELETED)), 400);
    public static final ErrorCode UNEXPECTED_USER_ERROR = new ErrorCode("13", Collections.singletonList(getMessage("UNEXPECTED_USER_ERROR", UserErrorMessages.UNEXPECTED_USER_ERROR)), 400);
    public static final ErrorCode NO_USER_EXISTS = new ErrorCode("14", Collections.singletonList(getMessage("NO_USER_EXISTS", UserErrorMessages.NO_USER_EXISTS)), 400);
    public static final ErrorCode USER_NOT_LEGIT = new ErrorCode("15", Collections.singletonList(getMessage("USER_NOT_LEGIT", UserErrorMessages.USER_NOT_LEGIT)), 400);
    public static final ErrorCode EMAIL_OTP_ALREADY_GENERATED = new ErrorCode("00", Collections.singletonList(getMessage("EMAIL_OTP_ALREADY_GENERATED", UserErrorMessages.EMAIL_OTP_ALREADY_GENERATED)), 400);
    public static final ErrorCode UNEXPECTED_EMAIL_OTP_ERROR = new ErrorCode("16", Collections.singletonList(getMessage("UNEXPECTED_EMAIL_OTP_ERROR", UserErrorMessages.UNEXPECTED_EMAIL_OTP_ERROR)), 400);
    public static final ErrorCode UNEXPECTED_STATS_ERROR = new ErrorCode("17", Collections.singletonList(getMessage("UNEXPECTED_STATS_ERROR", UserErrorMessages.UNEXPECTED_STATS_ERROR)), 500);
    public static final ErrorCode INVALID_UPI_ID = new ErrorCode("18", Collections.singletonList(getMessage("INVALID_UPI_ID", UserErrorMessages.INVALID_UPI_ID)), 500);
}
