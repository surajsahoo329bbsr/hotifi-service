package com.api.hotifi.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingTransfer {

    public Long sellerId;

    public String linkedAccountId;

    public Long amountInPaise;

    public String currency;

}
