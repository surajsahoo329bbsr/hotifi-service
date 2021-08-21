package com.api.hotifi.payment.error;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class SellerPaymentErrorCodes extends ErrorCodes {
    //Internal Error Codes
    public static final ErrorCode UNEXPECTED_SELLER_PAYMENT_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_SELLER_PAYMENT_ERROR", SellerPaymentErrorMessages.UNEXPECTED_SELLER_PAYMENT_ERROR)), 500);
    public static final ErrorCode UNEXPECTED_SELLER_RECEIPT_ERROR = new ErrorCode("01", Collections.singletonList(getMessage("UNEXPECTED_SELLER_RECEIPT_ERROR", SellerPaymentErrorMessages.UNEXPECTED_SELLER_RECEIPT_RESPONSE)), 500);
    public static final ErrorCode SELLER_NOT_LEGIT = new ErrorCode("02", Collections.singletonList(getMessage("SELLER_NOT_LEGIT", SellerPaymentErrorMessages.SELLER_NOT_LEGIT)), 500);
    public static final ErrorCode WITHDRAW_AMOUNT_PERIOD_ERROR = new ErrorCode("03", Collections.singletonList(getMessage("WITHDRAW_AMOUNT_PERIOD_ERROR", SellerPaymentErrorMessages.WITHDRAW_AMOUNT_PERIOD_ERROR)), 500);
    public static final ErrorCode MINIMUM_WITHDRAWAL_AMOUNT_ERROR = new ErrorCode("04", Collections.singletonList(getMessage("MINIMUM_WITHDRAWAL_AMOUNT_ERROR", SellerPaymentErrorMessages.MINIMUM_WITHDRAWAL_AMOUNT_ERROR)), 500);
    public static final ErrorCode SELLER_PAYMENT_NOT_FOUND = new ErrorCode("05", Collections.singletonList(getMessage("SELLER_PAYMENT_NOT_FOUND", SellerPaymentErrorMessages.SELLER_PAYMENT_NOT_FOUND)), 500);
    public static final ErrorCode SELLER_RECEIPT_NOT_FOUND = new ErrorCode("06", Collections.singletonList(getMessage("SELLER_RECEIPT_NOT_FOUND", SellerPaymentErrorMessages.SELLER_RECEIPT_NOT_FOUND)), 500);
    public static final ErrorCode NO_PENDING_TRANSFERS_CLAIMED = new ErrorCode("07", Collections.singletonList(getMessage("NO_PENDING_TRANSFERS_CLAIMED", SellerPaymentErrorMessages.NO_PENDING_TRANSFERS_CLAIMED)), 500);
    public static final ErrorCode SELLER_PAYMENT_UPI_FAILED = new ErrorCode("08", Collections.singletonList(getMessage("SELLER_PAYMENT_UPI_FAILED", SellerPaymentErrorMessages.SELLER_PAYMENT_UPI_FAILED)), 500);
}
