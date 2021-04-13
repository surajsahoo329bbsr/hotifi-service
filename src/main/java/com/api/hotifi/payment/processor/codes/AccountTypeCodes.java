package com.api.hotifi.payment.processor.codes;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public enum AccountTypeCodes {

    PRIVATE_LIMITED,
    PROPRIETORSHIP,
    PARTNERSHIP,
    INDIVIDUAL,
    PUBLIC_LIMITED,
    LLP,
    TRUST,
    SOCIETY,
    NGO;

    private static final Map<Integer, AccountTypeCodes> accountTypeCodes = new TreeMap<>();

    private static final int START_VALUE = BusinessConfigurations.ACCOUNT_TYPE_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i;
            accountTypeCodes.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static AccountTypeCodes fromInt(int i) {
        return accountTypeCodes.get(i);
    }

    public int value() {
        return value;
    }
}
