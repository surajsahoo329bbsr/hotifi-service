package com.api.hotifi.payment.processor.codes;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public enum PaymentGatewayCodes {

    RAZORPAY,
    STRIPE,
    PAYPAL;

    private static final Map<Integer, PaymentGatewayCodes> paymentGatewayMap = new TreeMap<>();
    private static final int START_VALUE = BusinessConfigurations.PAYMENT_GATEWAY_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i;
            paymentGatewayMap.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static PaymentGatewayCodes fromInt(int i) {
        return paymentGatewayMap.get(i);
    }

    public int value() {
        return value;
    }
}
