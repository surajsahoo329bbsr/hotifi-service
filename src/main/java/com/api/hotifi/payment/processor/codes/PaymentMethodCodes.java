package com.api.hotifi.payment.processor.codes;

import com.api.hotifi.common.constant.Constants;

import java.util.Map;
import java.util.TreeMap;

public enum PaymentMethodCodes {

    UPI_PAYMENT,
    NETBANKING_PAYMENT,
    DEBIT_CARD_PAYMENT,
    CREDIT_CARD_PAYMENT;

    private static final Map<Integer, PaymentMethodCodes> paymentMethodUpi = new TreeMap<>();

    private static final int START_VALUE = Constants.PAYMENT_METHOD_START_VALUE_CODE;

    static {
        for (int i = 0; i < values().length; i++) {
            values()[i].value = START_VALUE + i;
            paymentMethodUpi.put(values()[i].value, values()[i]);
        }
    }

    private int value;

    public static PaymentMethodCodes fromInt(int i) {
        return paymentMethodUpi.get(i);
    }

    public int value() {
        return value;
    }

}
