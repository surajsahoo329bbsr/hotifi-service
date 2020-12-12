package com.api.hotifi.identity.error;

import com.api.hotifi.common.exception.error.ErrorMessages;

public class UserErrorMessages extends ErrorMessages {
    public static final String USER_EXISTS = "User exists";
    public static final String NO_USER_EXISTS = "User doesn't exist";
    public static final String OTP_ALREADY_GENERATED = "OTP already generated.";
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String INVALID_OTP = "Inavlid otp";
    public static final String TOKEN_EXPIRED = "Token has expired.";
    public static final String USER_ALREADY_VERIFIED = "User already verified";
    public static final String USER_ALREADY_NOT_VERIFIED = "User already not verified";
    public static final String USER_ALREADY_ACTIVATED = "User already activated";
    public static final String USER_ALREADY_NOT_ACTIVATED = "User already not activated";
    public static final String USER_ALREADY_FREEZED = "User already freezed";
    public static final String USER_ALREADY_NOT_FREEZED = "User already not freezed";
    public static final String USER_ALREADY_BANNED = "User already banned";
    public static final String USER_ALREADY_NOT_BANNED = "User already not banned";
    public static final String USER_ALREADY_DELETED = "User already deleted";
    public static final String USER_EMAIL_ALREADY_VERIFIED = "User email already verified";
    public static final String USER_EMAIL_ALREADY_NOT_VERIFIED = "User email already not verified";
    public static final String USER_ALREADY_NOT_DELETED = "User already not deleted";
}