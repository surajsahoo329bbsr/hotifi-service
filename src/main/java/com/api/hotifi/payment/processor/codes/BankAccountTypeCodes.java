package com.api.hotifi.payment.processor.codes;

import com.api.hotifi.common.constant.Constants;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public enum BankAccountTypeCodes {

    CURRENT_ACCOUNT,
    SAVINGS_ACCOUNT,
    SALARY_ACCOUNT,
    FIXED_DEPOSIT_ACCOUNT,
    RECURRING_DEPOSIT_ACCOUNT,
    NRI_ACCOUNT,
    DEMAT_ACCOUNT,
    OTHERS;

    private static final Map<Integer, BankAccountTypeCodes> bankAccountTypeCodes = new TreeMap<>();

    private static final int START_VALUE = Constants.BANK_ACCOUNT_TYPE_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i;
            bankAccountTypeCodes.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static BankAccountTypeCodes fromInt(int i) {
        return bankAccountTypeCodes.get(i);
    }

    public int value() {
        return value;
    }
}
