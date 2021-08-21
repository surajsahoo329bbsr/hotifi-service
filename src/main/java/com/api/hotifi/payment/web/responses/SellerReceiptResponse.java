package com.api.hotifi.payment.web.responses;

import com.api.hotifi.payment.entities.SellerReceipt;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SellerReceiptResponse {

    private SellerReceipt sellerReceipt;

    private String sellerLinkedAccountId;

    private String upiId;

    private String hotifiBankAccount;

    private boolean isOnHold;

    private Date onHoldUntil;

}
