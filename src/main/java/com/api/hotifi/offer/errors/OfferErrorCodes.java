package com.api.hotifi.offer.errors;

import com.api.hotifi.common.exception.errors.ErrorCode;

import java.util.Collections;

import static com.api.hotifi.common.exception.errors.ErrorCodes.getMessage;

public class OfferErrorCodes {

    public static final ErrorCode UNEXPECTED_OFFER_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_OFFER_ERROR", OfferErrorMessages.UNEXPECTED_OFFER_ERROR)), 500);
    public static final ErrorCode OFFER_NOT_FOUND = new ErrorCode("01", Collections.singletonList(getMessage("OFFER_NOT_FOUND", OfferErrorMessages.OFFER_NOT_FOUND)), 500);
    public static final ErrorCode ACTIVATE_OFFER_UPDATE_ERROR = new ErrorCode("02", Collections.singletonList(getMessage("ACTIVATE_OFFER_UPDATE_ERROR", OfferErrorMessages.ACTIVATE_OFFER_UPDATE_ERROR)), 500);
    public static final ErrorCode OFFER_ALREADY_DEACTIVATED = new ErrorCode("03", Collections.singletonList(getMessage("OFFER_ALREADY_DEACTIVATED", OfferErrorMessages.OFFER_ALREADY_DEACTIVATED)), 500);
    public static final ErrorCode OFFER_ACTIVATION_AFTER_EXPIRY = new ErrorCode("04", Collections.singletonList(getMessage("OFFER_ACTIVATION_AFTER_EXPIRY", OfferErrorMessages.OFFER_ACTIVATION_AFTER_EXPIRY)), 500);

}
