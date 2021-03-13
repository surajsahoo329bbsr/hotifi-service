package com.api.hotifi.payment.processor.codes;

import com.api.hotifi.common.constant.Constants;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public enum SellerPaymentCodes {

    PAYMENT_CREATED,
    PAYMENT_FAILED,
    PAYMENT_PROCESSED;

    private static final Map<Integer, SellerPaymentCodes> sellerPaymentMap = new TreeMap<>();

    private static final int START_VALUE = Constants.SELLER_PAYMENT_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i;
            sellerPaymentMap.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static SellerPaymentCodes fromInt(int i) {
        return sellerPaymentMap.get(i);
    }

    public int value() {
        return value;
    }
}
