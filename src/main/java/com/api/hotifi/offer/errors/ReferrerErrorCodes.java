package com.api.hotifi.offer.errors;

import com.api.hotifi.common.exception.errors.ErrorCode;

import java.util.Collections;

import static com.api.hotifi.common.exception.errors.ErrorCodes.getMessage;

public class ReferrerErrorCodes {

    public static final ErrorCode UNEXPECTED_REFERRER_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_REFERRER_ERROR", ReferrerErrorMessages.UNEXPECTED_REFERRER_ERROR)), 500);
    public static final ErrorCode REFERRAL_ALREADY_GENERATED = new ErrorCode("01", Collections.singletonList(getMessage("REFERRAL_ALREADY_GENERATED", ReferrerErrorMessages.REFERRAL_ALREADY_GENERATED)), 500);
    public static final ErrorCode REFERRAL_OFFER_INACTIVE = new ErrorCode("02", Collections.singletonList(getMessage("REFERRAL_OFFER_INACTIVE", ReferrerErrorMessages.REFERRAL_OFFER_INACTIVE)), 500);

}
