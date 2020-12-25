package com.api.hotifi.payment.web.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class WifiSummaryResponse {

    private String sellerPhotoUrl;

    private String sellerName;

    private Date sessionStartedAt;

    private Date sessionFinishedAt;

    private double amountPaid;

    private double amountRefund;

    private double dataUsed;

    private int dataBought;

}
