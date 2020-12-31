package com.api.hotifi.payment.web.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BuyerStatsResponse {

    public double totalPendingRefunds;

    public double totalDataSold;

    public double dataBoughtByWifi;

    public double dataBoughtByMobileNetwork;

}
