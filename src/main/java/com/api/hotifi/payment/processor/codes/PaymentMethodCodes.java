package com.api.hotifi.payment.processor.codes;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.payment.error.PurchaseErrorCodes;

import java.util.Map;
import java.util.TreeMap;

public enum PaymentMethodCodes {

    UPI_PAYMENT_METHOD,
    NETBANKING,
    DEBIT_CARD_PAYMENT_METHOD,
    CREDIT_CARD_PAYMRNT_METHOD;

    private static final Map<Integer, PaymentMethodCodes> paymentMethodUpi = new TreeMap<>();

    private static final int START_VALUE = Constants.PAYMENT_METHOD_START_VALUE_CODE;

    static {
        if(values()[0].upiPaymentCodes == null){
            throw new HotifiException(PurchaseErrorCodes.UPI_PAYMENT_NULL_ERROR);
        }
        for (int i = 0; i < values().length; i++) {
            values()[i].value = START_VALUE + i;
            paymentMethodUpi.put(values()[i].value, values()[i]);
        }
    }

    private UpiPaymentCodes upiPaymentCodes;

    private int value;

    public static PaymentMethodCodes fromInt(int i) {
        return paymentMethodUpi.get(i);
    }

    public int value() {
        return value;
    }

    public UpiPaymentCodes getUpiPaymentMethod() {
        return upiPaymentCodes;
    }

    public void setUpiPaymentMethod(UpiPaymentCodes upiPaymentCodes) {
        this.upiPaymentCodes = upiPaymentCodes;
    }

}
