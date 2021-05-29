package com.api.hotifi.payment.utils;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.processor.codes.BuyerPaymentCodes;
import com.api.hotifi.session.entity.Session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class PaymentUtils {

    public static int getDataUsedSumOfSession(Session session) {
        List<Purchase> purchases = session.getPurchases();
        Supplier<Stream<Purchase>> dataStreamSupplier = purchases::stream;
        double finishedDataSum = dataStreamSupplier.get()
                .filter(p -> p.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.FINISH_WIFI_SERVICE.value())
                .mapToDouble(Purchase::getDataUsed).sum();
        double activeDataSum = dataStreamSupplier.get()
                .filter(p -> p.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.PAYMENT_CAPTURED.value() && p.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE < BuyerPaymentCodes.FINISH_WIFI_SERVICE.value())
                .mapToDouble(Purchase::getData).sum();
        return (int) Math.ceil(activeDataSum + finishedDataSum);
    }

    public static boolean isSellerPaymentDue(Date currentTime, Date lastPaidAt) {
        long timeDifference = currentTime.getTime() - lastPaidAt.getTime();
        long daysDifference = timeDifference / (24 * 60 * 60 * 1000);
        return daysDifference >= BusinessConfigurations.MINIMUM_SELLER_WITHDRAWAL_DUE_DAYS;
    }

    public static boolean isBuyerRefundDue(Date currentTime, Date lastPaidAt) {
        long timeDifference = currentTime.getTime() - lastPaidAt.getTime();
        long hoursDifference = timeDifference / (60 * 60 * 1000);
        return hoursDifference >= BusinessConfigurations.MAXIMUM_BUYER_REFUND_DUE_HOURS;
    }

    public static BigDecimal getInrFromPaise(int paise){
        return BigDecimal.valueOf(paise/ BusinessConfigurations.UNIT_INR_IN_PAISE)
                .setScale(2, RoundingMode.CEILING);
    }

    public static int getPaiseFromInr(BigDecimal ruppee){
        return ruppee.multiply(BigDecimal.valueOf(100)).intValue();
    }

    public static BigDecimal divideThenMultiplyCeilingTwoScale(BigDecimal numerator, BigDecimal denominator, BigDecimal multiplier) {
        return
                numerator
                        .multiply(multiplier)
                        .divide(denominator, 2, RoundingMode.CEILING)
                        .setScale(2, RoundingMode.CEILING);
    }

    public static BigDecimal divideThenMultiplyCeilingZeroScale(BigDecimal numerator, BigDecimal denominator, BigDecimal multiplier) {
        return
                numerator
                        .multiply(multiplier)
                        .divide(denominator, 2, RoundingMode.CEILING)
                        .setScale(0, RoundingMode.CEILING);
    }

    public static BigDecimal divideThenMultiplyFloorTwoScale(BigDecimal numerator, BigDecimal denominator, BigDecimal multiplier) {
        return
                numerator
                        .multiply(multiplier)
                        .divide(denominator, 2, RoundingMode.FLOOR)
                        .setScale(2, RoundingMode.FLOOR);
    }

    public static BigDecimal divideThenMultiplyFloorZeroScale(BigDecimal numerator, BigDecimal denominator, BigDecimal multiplier) {
        return
                numerator
                        .multiply(multiplier)
                        .divide(denominator, 2, RoundingMode.FLOOR)
                        .setScale(0, RoundingMode.CEILING);
    }

 }
