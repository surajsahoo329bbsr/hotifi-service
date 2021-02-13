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

    //4.0 or 4.5 format
    public String averageRating;

    public double totalDataSold;

    public double totalDataSoldByWifi;

    public double totalDataSoldByMobile;

}
