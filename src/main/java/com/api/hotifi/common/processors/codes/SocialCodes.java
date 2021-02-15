package com.api.hotifi.common.processors.codes;

import com.api.hotifi.common.constant.Constants;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public enum SocialCodes {

    GOOGLE, //100
    FACEBOOK, //101
    TWITTER, //102...and so on
    GITHUB,
    MICROSOFT,
    APPLE;

    private static final Map<Integer, SocialCodes> socialCodes = new TreeMap<>();

    private static final int START_VALUE = Constants.SOCIAL_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i;
            socialCodes.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static SocialCodes fromInt(int i) {
        return socialCodes.get(i);
    }

    public int value() {
        return value;
    }
}
