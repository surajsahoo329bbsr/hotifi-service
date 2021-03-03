package com.api.hotifi.payment.processor.codes;

import com.api.hotifi.common.constant.Constants;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public enum PaymentMethodCodes {

    UPI,
    NETBANKING,
    CREDIT,
    DEBIT;

    private static final Map<Integer, PaymentMethodCodes> paymentMethodUpi = new TreeMap<>();

    private static final int START_VALUE = Constants.PAYMENT_METHOD_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i * Constants.PAYMENT_METHOD_START_VALUE_CODE;
            paymentMethodUpi.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static PaymentMethodCodes fromInt(int i) {
        return paymentMethodUpi.get(i);
    }

    public int value() {
        return value;
    }

}
