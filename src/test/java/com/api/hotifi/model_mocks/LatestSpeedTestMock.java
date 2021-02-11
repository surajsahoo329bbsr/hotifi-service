package com.api.hotifi.model_mocks;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LatestSpeedTestMock {

    private final Long userId;

    private final String pinCode;

    private final boolean isWifi;

}
