package com.api.hotifi.payment.utils;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.session.entity.Session;

import java.util.Date;
import java.util.List;

public class PaymentUtils {

    public static int getDataUsedSumOfSession(Session session){
        List<Purchase> purchases = session.getPurchases();
        double dataSum = purchases.stream()
                .filter(p -> p.getDataUsed() >= 0.0)
                .mapToDouble(Purchase::getDataUsed).sum();
        return (int) Math.ceil(dataSum);
    }

    public static boolean isSellerPaymentDue(Date currentTime, Date lastPaidAt){
        long timeDifference =  currentTime.getTime() - lastPaidAt.getTime();
        long daysDifference = timeDifference / (24 * 60 * 60 * 1000);
        return daysDifference >= Constants.MINIMUM_WITHDRAWAL_DUE_DAYS; // If otp generated is more than 10 minutes
    }
}
