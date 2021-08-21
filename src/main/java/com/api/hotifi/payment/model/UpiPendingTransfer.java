package com.api.hotifi.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class UpiPendingTransfer {

    public Long sellerId;

    public String upiId;

    public BigDecimal amount;

    public String currency;

}
