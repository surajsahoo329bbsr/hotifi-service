package com.api.hotifi.payment.web.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PendingMoneyResponse {

    private boolean sellerAmountDue;

    private BigDecimal sellerAmount;

    private boolean isTransferPending;

    private String transferErrorDescription;

    private Date sellerAmountLastPaidAt;

    private boolean buyerRefundDue;

    private BigDecimal buyerRefund;

    private Date buyerOldestPendingRefundAt;

}
