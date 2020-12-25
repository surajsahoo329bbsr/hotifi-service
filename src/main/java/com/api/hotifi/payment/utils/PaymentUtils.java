package com.api.hotifi.payment.utils;

import com.api.hotifi.payment.entity.Purchase;
import com.api.hotifi.session.entity.Session;

import java.util.List;

public class PaymentUtils {
    public static int getDataUsedSumOfSession(Session session){
        List<Purchase> purchases = session.getPurchases();
        double dataSum = purchases.stream()
                .filter(p -> p.getDataUsed() >= 0.0)
                .mapToDouble(Purchase::getDataUsed).sum();
        return (int) Math.ceil(dataSum);
    }
}
