package com.api.hotifi.common.processors.codes;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public enum CloudClientCodes {

    GOOGLE_CLOUD_PLATFORM, //100
    AMAZON_WEB_SERVICES, //101
    AZURE, //102
    TWILIO; //103

    private static final Map<Integer, CloudClientCodes> notificationClientCodes = new TreeMap<>();

    private static final int START_VALUE = BusinessConfigurations.CLOUD_CLIENT_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i;
            notificationClientCodes.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static CloudClientCodes fromInt(int i) {
        return notificationClientCodes.get(i);
    }

    public int value() {
        return value;
    }
}
