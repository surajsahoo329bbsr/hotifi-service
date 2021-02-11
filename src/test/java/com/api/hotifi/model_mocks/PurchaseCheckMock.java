package com.api.hotifi.model_mocks;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PurchaseCheckMock {

    private final Long buyerId;

    private final Long sessionId;

    private final Long dataToBeUsed;

}
