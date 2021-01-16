package com.api.hotifi.payment.web.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class SellerStatsResponse {

    public Long sellerPaymentId;

    public Date lastPaidAt;

    public double totalEarnings;

    public double totalAmountWithdrawn;

    //4.0 or 4.5 format
    public String averageRating;

    public double totalDataSold;

    public double totalDataSoldByWifi;

    public double totalDataSoldByMobile;

}
