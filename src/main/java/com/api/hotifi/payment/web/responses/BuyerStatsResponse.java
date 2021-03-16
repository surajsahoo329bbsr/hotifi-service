package com.api.hotifi.payment.web.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BuyerStatsResponse {

    public BigDecimal totalPendingRefunds;

    public BigDecimal totalRefundsProcessed;

    public double totalDataBought;

    public double totalDataBoughtByWifi;

    public double totalDataBoughtByMobile;

}
