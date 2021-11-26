package com.api.hotifi.payment.processor.codes;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public enum OrderStatusCodes {

    CREATED,
    ATTEMPTED,
    PAID;

    private static final Map<Integer, OrderStatusCodes> orderStatusCodes = new TreeMap<>();

    private static final int START_VALUE = BusinessConfigurations.RAZORPAY_ORDER_STATUS_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i;
            orderStatusCodes.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static OrderStatusCodes fromInt(int i) {
        return orderStatusCodes.get(i);
    }

    public int value() {
        return value;
    }
}
