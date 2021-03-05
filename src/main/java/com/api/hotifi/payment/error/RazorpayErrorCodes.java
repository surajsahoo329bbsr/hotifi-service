package com.api.hotifi.payment.error;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class RazorpayErrorCodes extends ErrorCodes {

    //Not found status
    public static final ErrorCode INVALID_CLIENT_CREDENTIALS = new ErrorCode("00", Collections.singletonList(getMessage("INVALID_CLIENT_CREDENTIALS", RazorpayErrorMessages.INVALID_CLIENT_CREDENTIALS)), 400);
    public static final ErrorCode PAYMENT_NOT_FOUND = new ErrorCode("02", Collections.singletonList(getMessage("PAYMENT_NOT_FOUND", RazorpayErrorMessages.PAYMENT_NOT_FOUND)), 400);
    public static final ErrorCode PAYMENTS_OF_LINKED_ACCOUNT_NOT_FOUND = new ErrorCode("03", Collections.singletonList(getMessage("PAYMENTS_OF_LINKED_ACCOUNT_NOT_FOUND", RazorpayErrorMessages.PAYMENTS_OF_LINKED_ACCOUNT_NOT_FOUND)), 400);
    public static final ErrorCode SPECIFIC_REFUND_NOT_FOUND = new ErrorCode("04", Collections.singletonList(getMessage("SPECIFIC_REFUND_NOT_FOUND", RazorpayErrorMessages.SPECIFIC_REFUND_NOT_FOUND)), 400);
    public static final ErrorCode REFUND_NOT_FOUND = new ErrorCode("05", Collections.singletonList(getMessage("REFUND_NOT_FOUND", RazorpayErrorMessages.REFUND_NOT_FOUND)), 400);
    public static final ErrorCode TRANSFER_NOT_FOUND = new ErrorCode("06", Collections.singletonList(getMessage("TRANSFER_NOT_FOUND", RazorpayErrorMessages.TRANSFER_NOT_FOUND)), 400);

    //Failure status
    public static final ErrorCode CAPTURE_PAYMENT_FAILED = new ErrorCode("01", Collections.singletonList(getMessage("CAPTURE_PAYMENT_FAILED", RazorpayErrorMessages.CAPTURE_PAYMENT_FAILED)), 500);
    public static final ErrorCode NORMAL_FULL_REFUND_FAILED = new ErrorCode("02", Collections.singletonList(getMessage("NORMAL_FULL_REFUND_FAILED", RazorpayErrorMessages.NORMAL_FULL_REFUND_FAILED)), 500);
    public static final ErrorCode NORMAL_PARTIAL_REFUND_FAILED = new ErrorCode("03", Collections.singletonList(getMessage("NORMAL_PARTIAL_REFUND_FAILED", RazorpayErrorMessages.NORMAL_PARTIAL_REFUND_FAILED)), 500);
    public static final ErrorCode TRANSFER_FAILED = new ErrorCode("04", Collections.singletonList(getMessage("TRANSFER_FAILED", RazorpayErrorMessages.TRANSFER_FAILED)), 400);

}
