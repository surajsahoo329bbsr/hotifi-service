package com.api.hotifi.payment.processor.codes;

import com.api.hotifi.common.constant.Constants;

import java.util.Map;
import java.util.TreeMap;

public enum UpiPaymentCodes {

    GOOGLE_PAY,
    PHONE_PE,
    PAYTM,
    OTHERS;

    private static final Map<Integer, UpiPaymentCodes> upiPaymentMap = new TreeMap<>();

    private static final int START_VALUE = Constants.UPI_PAYMENT_START_VALUE_CODE;

    static {
        for (int i = 0; i < values().length; i++) {
            values()[i].value = START_VALUE + i;
            upiPaymentMap.put(values()[i].value, values()[i]);
        }
    }

    private int value;

    public static UpiPaymentCodes fromInt(int i) {
        return upiPaymentMap.get(i);
    }

    public int value() {
        return value;
    }

}
