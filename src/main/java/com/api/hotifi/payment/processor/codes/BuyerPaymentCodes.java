package com.api.hotifi.payment.processor.codes;

import com.api.hotifi.common.constant.Constants;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public enum BuyerPaymentCodes {

    PAYMENT_FAILED,
    PAYMENT_PROCESSING,
    PAYMENT_SUCCESSFUL,
    START_WIFI_SERVICE,
    UPDATE_WIFI_SERVICE,
    FINISH_WIFI_SERVICE,
    REFUND_STARTED,
    REFUND_FAILED,
    REFUND_PROCESSING,
    REFUND_SUCCESSFUL;

    private static final Map<Integer, BuyerPaymentCodes> buyerPaymentMap = new TreeMap<>();

    private static final int START_VALUE = Constants.BUYER_PAYMENT_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i;
            buyerPaymentMap.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static BuyerPaymentCodes fromInt(int i) {
        return buyerPaymentMap.get(i);
    }

    public int value() {
        return value;
    }
}
