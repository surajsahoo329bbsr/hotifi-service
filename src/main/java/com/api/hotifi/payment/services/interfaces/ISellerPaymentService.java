package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.identity.entities.User;

public interface ISellerPaymentService {

    void addSellerPayment(User seller, double amountEarned);

    void updateSellerPayment(User seller, double amountEarned);

    //<Add-Response-Entity-here> updateSellerPayment(Long sellerId);

}
