package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;

public interface ISellerPaymentService {

    void addSellerPayment(User seller, double amountEarned);

    void updateSellerPayment(User seller, double amountEarned);

    SellerReceiptResponse withdrawSellerPayment(Long sellerId);

    //<Add-Response-Entity-here> getSellerStats(Long sellerId);

}
