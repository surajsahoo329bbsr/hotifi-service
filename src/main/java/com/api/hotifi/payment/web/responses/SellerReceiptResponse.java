package com.api.hotifi.payment.web.responses;

import com.api.hotifi.payment.entities.SellerReceipt;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerReceiptResponse {

    private SellerReceipt sellerReceipt;

    private String sellerLinkedAccountId;

    private String hotifiBankAccount;

}
