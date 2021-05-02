package com.api.hotifi.speedtest.codes;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public enum NetworkProviderCodes {

    BROADBAND,
    JIO,
    AIRTEL,
    BSNL,
    VODAFONE,
    OTHERS;

    private static final Map<Integer, NetworkProviderCodes> networkProviderCodes = new TreeMap<>();

    private static final int START_VALUE = BusinessConfigurations.NETWORK_PROVIDER_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i;
            networkProviderCodes.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static NetworkProviderCodes fromInt(int i) {
        return networkProviderCodes.get(i);
    }

    public int value() {
        return value;
    }
}

