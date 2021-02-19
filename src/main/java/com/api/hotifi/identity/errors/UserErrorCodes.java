package com.api.hotifi.identity.errors;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class UserErrorCodes extends ErrorCodes {
    public static final ErrorCode UNEXPECTED_USER_ERROR = new ErrorCode("01", Collections.singletonList(getMessage("UNEXPECTED_USER_ERROR", UserErrorMessages.UNEXPECTED_USER_ERROR)), 400);
    public static final ErrorCode UNEXPECTED_EMAIL_OTP_ERROR = new ErrorCode("02", Collections.singletonList(getMessage("UNEXPECTED_EMAIL_OTP_ERROR", UserErrorMessages.UNEXPECTED_EMAIL_OTP_ERROR)), 400);
    public static final ErrorCode FACEBOOK_USER_EXISTS = new ErrorCode("03", Collections.singletonList(getMessage("FACEBOOK_USER_EXISTS", UserErrorMessages.FACEBOOK_USER_EXISTS)), 400);
    public static final ErrorCode GOOGLE_USER_EXISTS = new ErrorCode("04", Collections.singletonList(getMessage("GOOGLE_USER_EXISTS", UserErrorMessages.GOOGLE_USER_EXISTS)), 400);
    public static final ErrorCode USERNAME_EXISTS = new ErrorCode("05", Collections.singletonList(getMessage("USERNAME_EXISTS", UserErrorMessages.USERNAME_EXISTS)), 400);
    public static final ErrorCode USER_EXISTS = new ErrorCode("06", Collections.singletonList(getMessage("USER_EXISTS", UserErrorMessages.USER_EXISTS)), 400);
    public static final ErrorCode INVALID_TOKEN = new ErrorCode("07", Collections.singletonList(getMessage("INVALID_TOKEN", UserErrorMessages.INVALID_TOKEN)), 400);
    public static final ErrorCode TOKEN_EXPIRED = new ErrorCode("08", Collections.singletonList(getMessage("TOKEN_EXPIRED", UserErrorMessages.TOKEN_EXPIRED)), 400);
    public static final ErrorCode USER_ALREADY_VERIFIED = new ErrorCode("09", Collections.singletonList(getMessage("USER_ALREADY_VERIFIED", UserErrorMessages.USER_ALREADY_VERIFIED)), 400);

    public static final ErrorCode USER_ALREADY_LOGGED_IN = new ErrorCode("10", Collections.singletonList(getMessage("USER_ALREADY_LOGGED_IN", UserErrorMessages.USER_ALREADY_LOGGED_IN)), 400);
    public static final ErrorCode USER_ALREADY_LOGGED_OUT = new ErrorCode("11", Collections.singletonList(getMessage("USER_ALREADY_LOGGED_OUT", UserErrorMessages.USER_ALREADY_LOGGED_OUT)), 400);
    public static final ErrorCode USER_NOT_LOGGED_IN = new ErrorCode("12", Collections.singletonList(getMessage("USER_NOT_LOGGED_IN", UserErrorMessages.USER_NOT_LOGGED_IN)), 500);

    public static final ErrorCode USER_ALREADY_ACTIVATED = new ErrorCode("12", Collections.singletonList(getMessage("USER_ALREADY_ACTIVATED", UserErrorMessages.USER_ALREADY_ACTIVATED)), 400);
    public static final ErrorCode USER_NOT_ACTIVATED = new ErrorCode("13", Collections.singletonList(getMessage("USER_NOT_ACTIVATED", UserErrorMessages.USER_NOT_ACTIVATED)), 500);

    public static final ErrorCode USER_FREEZED = new ErrorCode("15", Collections.singletonList(getMessage("USER_FREEZED", UserErrorMessages.USER_FREEZED)), 500);
    public static final ErrorCode USER_ALREADY_FREEZED = new ErrorCode("13", Collections.singletonList(getMessage("USER_ALREADY_FREEZED", UserErrorMessages.USER_ALREADY_FREEZED)), 400);
    public static final ErrorCode USER_ALREADY_NOT_FREEZED = new ErrorCode("14", Collections.singletonList(getMessage("USER_ALREADY_NOT_FREEZED", UserErrorMessages.USER_ALREADY_NOT_FREEZED)), 400);

    public static final ErrorCode USER_BANNED = new ErrorCode("14", Collections.singletonList(getMessage("USER_BANNED", UserErrorMessages.USER_BANNED)), 500);
    public static final ErrorCode USER_ALREADY_BANNED = new ErrorCode("15", Collections.singletonList(getMessage("USER_ALREADY_BANNED", UserErrorMessages.USER_ALREADY_BANNED)), 400);
    public static final ErrorCode USER_ALREADY_NOT_BANNED = new ErrorCode("16", Collections.singletonList(getMessage("USER_ALREADY_NOT_BANNED", UserErrorMessages.USER_ALREADY_NOT_BANNED)), 400);

    public static final ErrorCode USER_DELETED = new ErrorCode("13", Collections.singletonList(getMessage("USER_DELETED", UserErrorMessages.USER_DELETED)), 500);
    public static final ErrorCode USER_ALREADY_DELETED = new ErrorCode("17", Collections.singletonList(getMessage("USER_ALREADY_DELETED", UserErrorMessages.USER_ALREADY_DELETED)), 400);
    public static final ErrorCode USER_NOT_FOUND = new ErrorCode("18", Collections.singletonList(getMessage("USER_NOT_FOUND", UserErrorMessages.USER_NOT_FOUND)), 400);
    public static final ErrorCode USER_NOT_LEGIT = new ErrorCode("19", Collections.singletonList(getMessage("USER_NOT_LEGIT", UserErrorMessages.USER_NOT_LEGIT)), 400);
    public static final ErrorCode EMAIL_OTP_ALREADY_GENERATED = new ErrorCode("20", Collections.singletonList(getMessage("EMAIL_OTP_ALREADY_GENERATED", UserErrorMessages.EMAIL_OTP_ALREADY_GENERATED)), 400);
    public static final ErrorCode INVALID_LINKED_ACCOUNT_ID = new ErrorCode("21", Collections.singletonList(getMessage("INVALID_LINKED_ACCOUNT_ID", UserErrorMessages.INVALID_LINKED_ACCOUNT_ID)), 500);
    public static final ErrorCode USER_LINKED_ACCOUNT_ID_NULL = new ErrorCode("22", Collections.singletonList(getMessage("USER_LINKED_ACCOUNT_ID_NULL", UserErrorMessages.USER_LINKED_ACCOUNT_ID_NULL)), 500);
    public static final ErrorCode USER_FORBIDDEN = new ErrorCode("01", Collections.singletonList(getMessage("USER_FORBIDDEN", UserErrorMessages.USER_FORBIDDEN)), 400);
    public static final ErrorCode USER_TOKEN_EXPIRED = new ErrorCode("02", Collections.singletonList(getMessage("USER_TOKEN_EXPIRED", UserErrorMessages.USER_TOKEN_EXPIRED)), 400);
    public static final ErrorCode USER_SOCIAL_TOKEN_OR_IDENTIFIER_NOT_FOUND = new ErrorCode("03", Collections.singletonList(getMessage("USER_SOCIAL_TOKEN_OR_IDENTIFIER_NOT_FOUND", UserErrorMessages.USER_SOCIAL_TOKEN_OR_IDENTIFIER_NOT_FOUND)), 400);
    public static final ErrorCode USER_SOCIAL_IDENTIFIER_INVALID = new ErrorCode("04", Collections.singletonList(getMessage("USER_SOCIAL_IDENTIFIER_INVALID", UserErrorMessages.USER_SOCIAL_IDENTIFIER_INVALID)), 400);
}
