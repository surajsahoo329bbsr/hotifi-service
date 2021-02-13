package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;

import java.math.BigDecimal;

public interface ISellerPaymentService {

    //DO NOT ADD TO CONTROLLER
    void addSellerPayment(User seller, BigDecimal amountEarned);

    //DO NOT ADD TO CONTROLLER
    void updateSellerPayment(User seller, BigDecimal amountEarned, boolean isUpdateTimeOnly);

    SellerReceiptResponse withdrawSellerPayment(Long sellerId);

}
