package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;

public interface ISellerPaymentService {

    //DO NOT ADD TO CONTROLLER
    void addSellerPayment(User seller, double amountEarned);

    //DO NOT ADD TO CONTROLLER
    void updateSellerPayment(User seller, double amountEarned);

    SellerReceiptResponse withdrawSellerPayment(Long sellerId);

}
