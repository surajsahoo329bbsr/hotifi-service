package com.api.hotifi.payment.error;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class PurchaseErrorCodes extends ErrorCodes {
    public static final ErrorCode UNEXPECTED_PURCHASE_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_PURCHASE_ERROR", PurchaseErrorMessages.UNEXPECTED_PURCHASE_ERROR)), 500);
    public static final ErrorCode BUYER_NOT_LEGIT = new ErrorCode("01", Collections.singletonList(getMessage("BUYER_NOT_LEGIT", PurchaseErrorMessages.BUYER_NOT_LEGIT)), 500);
    public static final ErrorCode SESSION_NOT_FOUND = new ErrorCode("02", Collections.singletonList(getMessage("SESSION_NOT_FOUND", PurchaseErrorMessages.SESSION_NOT_FOUND)), 500);
    public static final ErrorCode SESSION_ALREADY_ENDED = new ErrorCode("03", Collections.singletonList(getMessage("SESSION_ALREADY_ENDED", PurchaseErrorMessages.SESSION_ALREADY_ENDED)), 500);
    public static final ErrorCode EXCESS_DATA_TO_BUY_ERROR = new ErrorCode("04", Collections.singletonList(getMessage("EXCESS_DATA_TO_BUY_ERROR", PurchaseErrorMessages.EXCESS_DATA_TO_BUY_ERROR)), 500);
    public static final ErrorCode BUYER_SELLER_SAME = new ErrorCode("05", Collections.singletonList(getMessage("BUYER_SELLER_SAME", PurchaseErrorMessages.BUYER_SELLER_SAME)), 500);
    public static final ErrorCode WITHDRAW_PENDING_REFUNDS = new ErrorCode("06", Collections.singletonList(getMessage("WITHDRAW_PENDING_REFUNDS", PurchaseErrorMessages.WITHDRAW_PENDING_REFUNDS)), 500);
    public static final ErrorCode NO_PURCHASE_EXISTS = new ErrorCode("07", Collections.singletonList(getMessage("NO_PURCHASE_EXISTS", PurchaseErrorMessages.NO_PURCHASE_EXISTS)), 500);
    public static final ErrorCode BUYER_WIFI_SERVICE_ALREADY_STARTED = new ErrorCode("08", Collections.singletonList(getMessage("BUYER_WIFI_SERVICE_ALREADY_STARTED", PurchaseErrorMessages.BUYER_WIFI_SERVICE_ALREADY_STARTED)), 500);
    public static final ErrorCode BUYER_WIFI_SERVICE_ALREADY_FINISHED = new ErrorCode("09", Collections.singletonList(getMessage("BUYER_WIFI_SERVICE_ALREADY_FINISHED", PurchaseErrorMessages.BUYER_WIFI_SERVICE_ALREADY_FINISHED)), 500);
    public static final ErrorCode PURCHASE_UPDATE_NOT_LEGIT = new ErrorCode("10", Collections.singletonList(getMessage("PURCHASE_UPDATE_NOT_LEGIT", PurchaseErrorMessages.PURCHASE_UPDATE_NOT_LEGIT)), 500);
    public static final ErrorCode BUYER_WIFI_SERVICE_NOT_STARTED = new ErrorCode("11", Collections.singletonList(getMessage("BUYER_WIFI_SERVICE_NOT_STARTED", PurchaseErrorMessages.BUYER_WIFI_SERVICE_NOT_STARTED)), 500);
    public static final ErrorCode DATA_USED_EXCEEDS_DATA_BOUGHT = new ErrorCode("12", Collections.singletonList(getMessage("DATA_USED_EXCEEDS_DATA_BOUGHT", PurchaseErrorMessages.DATA_USED_EXCEEDS_DATA_BOUGHT)), 500);
    public static final ErrorCode DATA_TO_UPDATE_DECEEDS_DATA_USED = new ErrorCode("13", Collections.singletonList(getMessage("DATA_TO_UPDATE_DECEEDS_DATA_USED", PurchaseErrorMessages.DATA_TO_UPDATE_DECEEDS_DATA_USED)), 500);
    public static final ErrorCode PAYMENT_NOT_SUCCESSFUL = new ErrorCode("15", Collections.singletonList(getMessage("PAYMENT_NOT_SUCCESSFUL", PurchaseErrorMessages.PAYMENT_NOT_SUCCESSFUL)), 500);
    public static final ErrorCode NO_BUYER_PENDING_REFUNDS = new ErrorCode("16", Collections.singletonList(getMessage("NO_BUYER_PENDING_REFUNDS", PurchaseErrorMessages.NO_BUYER_PENDING_REFUNDS)), 500);
    public static final ErrorCode UPDATE_WIFI_SERVICE_BEFORE_FINISHING = new ErrorCode("17", Collections.singletonList(getMessage("UPDATE_WIFI_SERVICE_BEFORE_FINISHING", PurchaseErrorMessages.UPDATE_WIFI_SERVICE_BEFORE_FINISHING)), 500);
}
