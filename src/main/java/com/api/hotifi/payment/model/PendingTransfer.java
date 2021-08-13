package com.api.hotifi.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PendingTransfer {

    public Long sellerId;

    public String linkedAccountId;

    public Long amountInPaise;

    public String currency;

}
