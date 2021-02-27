package com.api.hotifi.identity.errors;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class AuthenticationErrorCodes extends ErrorCodes {

    //Client Error Codes
    public static final ErrorCode EMAIL_OTP_EXPIRED = new ErrorCode("00", Collections.singletonList(getMessage("EMAIL_OTP_EXPIRED", AuthenticationErrorMessages.EMAIL_OTP_EXPIRED)), 400);
    public static final ErrorCode EMAIL_OTP_INVALID = new ErrorCode("01", Collections.singletonList(getMessage("EMAIL_OTP_INVALID", AuthenticationErrorMessages.EMAIL_OTP_INVALID)), 400);
    public static final ErrorCode AUTHENTICATION_NOT_VERIFIED = new ErrorCode("02", Collections.singletonList(getMessage("AUTHENTICATION_NOT_VERIFIED", AuthenticationErrorMessages.AUTHENTICATION_NOT_VERIFIED)), 400);
    public static final ErrorCode AUTHENTICATION_NOT_LEGIT = new ErrorCode("03", Collections.singletonList(getMessage("AUTHENTICATION_NOT_LEGIT", AuthenticationErrorMessages.AUTHENTICATION_NOT_LEGIT)), 400);
    public static final ErrorCode FIREBASE_AUTH_EXCEPTION = new ErrorCode("04", Collections.singletonList(getMessage("FIREBASE_AUTH_EXCEPTION", AuthenticationErrorMessages.UNEXPECTED_AUTHENTICATION_ERROR)), 400);

    //Internal Error Codes
    public static final ErrorCode UNEXPECTED_AUTHENTICATION_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_AUTHENTICATION_ERROR", AuthenticationErrorMessages.UNEXPECTED_AUTHENTICATION_ERROR)), 500);
    public static final ErrorCode EMAIL_DOES_NOT_EXIST = new ErrorCode("01", Collections.singletonList(getMessage("EMAIL_NOT_FOUND", AuthenticationErrorMessages.EMAIL_NOT_FOUND)), 500);
    public static final ErrorCode EMAIL_ALREADY_EXISTS = new ErrorCode("02", Collections.singletonList(getMessage("EMAIL_ALREADY_EXISTS", AuthenticationErrorMessages.EMAIL_ALREADY_EXISTS)), 500);
    public static final ErrorCode EMAIL_ALREADY_VERIFIED = new ErrorCode("03", Collections.singletonList(getMessage("EMAIL_ALREADY_VERIFIED", AuthenticationErrorMessages.EMAIL_ALREADY_VERIFIED)), 500);
    public static final ErrorCode EMAIL_NOT_VERIFIED = new ErrorCode("04", Collections.singletonList(getMessage("EMAIL_NOT_VERIFIED", AuthenticationErrorMessages.EMAIL_NOT_VERIFIED)), 500);
    public static final ErrorCode PHONE_ALREADY_EXISTS = new ErrorCode("05", Collections.singletonList(getMessage("PHONE_ALREADY_EXISTS", AuthenticationErrorMessages.PHONE_ALREADY_EXISTS)), 500);
    public static final ErrorCode PHONE_ALREADY_VERIFIED = new ErrorCode("06", Collections.singletonList(getMessage("PHONE_ALREADY_VERIFIED", AuthenticationErrorMessages.PHONE_ALREADY_VERIFIED)), 500);

}
