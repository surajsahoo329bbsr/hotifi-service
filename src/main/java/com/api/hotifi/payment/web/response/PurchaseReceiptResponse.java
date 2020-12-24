package com.api.hotifi.payment.web.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PurchaseReceiptResponse {

    //Purchase Id and Payment Id are not same, while payment Id is the
    //exact Id got after successful/failure transaction externally,
    //purchaseId is the id generated from hotifi database

    private Long purchaseId;

    private int purchaseStatus;

    private double amountPaid;

    private Date createdAt;

    private String paymentId;

    private String hotifiUpiId;

    private String buyerUpiId;

}
