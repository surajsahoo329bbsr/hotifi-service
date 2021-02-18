package com.api.hotifi.payment.error;

import com.api.hotifi.common.constant.Constants;

public class SellerPaymentErrorMessages {
    public static final String UNEXPECTED_SELLER_PAYMENT_ERROR = "Unexpected seller payment error";
    public static final String UNEXPECTED_SELLER_RECEIPT_RESPONSE = "Unexpected seller receipt response";
    public static final String SELLER_PAYMENT_NOT_FOUND = "No seller payment exists";
    public static final String SELLER_NOT_LEGIT = "Seller not legit";
    public static final String WITHDRAW_AMOUNT_PERIOD_ERROR = "Withdraw amount period is not over";
    public static final String MINIMUM_WITHDRAWAL_AMOUNT_ERROR = "Minimum withdrawal is " + Constants.MINIMUM_AMOUNT_INR;
    //public static final String MINIMUM_AMOUNT_INR = "Minimum withdrawal amount must be at least INR " + Constants.MINIMUM_AMOUNT_INR;
    public static final String SELLER_RECEIPT_NOT_FOUND = "Seller receipt not found";
}
