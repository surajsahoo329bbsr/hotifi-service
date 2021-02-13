package com.api.hotifi.payment.web.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class WifiSummaryResponse {

    private RefundReceiptResponse refundReceiptResponse;

    private String sellerPhotoUrl;

    private String sellerName;

    private Date sessionStartedAt;

    private Date sessionFinishedAt;

    private BigDecimal amountPaid;

    private BigDecimal amountRefund;

    private double dataUsed;

    private double dataBought;

}
