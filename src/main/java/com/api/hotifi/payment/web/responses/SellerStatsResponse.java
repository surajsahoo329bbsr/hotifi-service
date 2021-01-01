package com.api.hotifi.payment.web.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellerStatsResponse {

    public double totalEarnings;

    public double totalAmountWithdrawn;

    //4.0 or 4.5 format
    public String averageRating;

    public double totalDataSold;

    public double totalDataSoldByWifi;

    public double totalDataSoldByMobile;

}
