package com.api.hotifi.payment.web.responses;

import com.api.hotifi.payment.entities.Purchase;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class RefundReceiptResponse {

    public Purchase purchase;

    public String refundPaymentId;

    public Date refundDoneAt;

    public String hotifiUpiId;

    public String buyerUpiId;

}
