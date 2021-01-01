package com.api.hotifi.payment.web.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BuyerStatsResponse {

    public double totalPendingRefunds;

    public double totalDataBought;

    public double totalDataBoughtByWifi;

    public double totalDataBoughtByMobile;

}
