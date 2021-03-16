package com.api.hotifi.payment.web.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@AllArgsConstructor
public class SellerStatsResponse {

    public Long sellerPaymentId;

    public Date lastPaidAt;

    public BigDecimal totalEarnings;

    public BigDecimal totalAmountWithdrawn;

    public double totalDataSold;

    public double totalDataSoldByWifi;

    public double totalDataSoldByMobile;

}
