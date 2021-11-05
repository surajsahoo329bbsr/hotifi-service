package com.api.hotifi.payment.utils;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.processor.codes.BuyerPaymentCodes;
import com.api.hotifi.payment.processor.codes.PaymentMethodCodes;
import com.api.hotifi.session.entity.Session;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class PaymentUtils {

    public static Date convertEpochToDate(long epochTime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(BusinessConfigurations.DATABASE_EPOCH_TO_DATE_PATTERN);
            String stringTime = String.valueOf(epochTime);
            return dateFormat.parse(stringTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date convertUtcToIst(Date utcDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(utcDate);
        calendar.add(Calendar.MINUTE, -330); // -330 minutes for -5:30 hours
        return calendar.getTime();
    }

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

    public static String hideBankAccountNumber(String bankAccountNumber) {
        if (bankAccountNumber == null || bankAccountNumber.length() < 5)
            return bankAccountNumber;
        int length = bankAccountNumber.length();
        int hiddenCharIndex = length - 5;
        //concatenating X with last 4 digits of number
        return "X".repeat(hiddenCharIndex + 1) + bankAccountNumber.substring(hiddenCharIndex + 1);
    }

    public static boolean isSellerPaymentDue(Date currentTime, Date lastPaidAt) {
        long timeDifference = currentTime.getTime() - lastPaidAt.getTime();
        long hoursDifference = timeDifference / (60 * 60 * 1000);
        return hoursDifference >= BusinessConfigurations.MINIMUM_SELLER_WITHDRAWAL_HOURS;
    }

    public static String getPaymentTransactionId(PaymentMethodCodes paymentMethodCode, JSONObject acquirerDataJson) {

        String paymentTransactionId = "Not available";
        switch (paymentMethodCode) {
            case UPI:
                if (!acquirerDataJson.isNull("rrn"))
                    return acquirerDataJson.getString("rrn");
            case NETBANKING:
                if (!acquirerDataJson.isNull("bank_transaction_id"))
                    return acquirerDataJson.getString("bank_transaction_id");
            case WALLET:
                if (!acquirerDataJson.isNull("wallet"))
                    return acquirerDataJson.getString("wallet").toUpperCase();
            case CARD:
            case EMI:
                break;
        }
        return paymentTransactionId;
    }

    public static String getRefundTransactionId(JSONObject acquirerDataJson) {
        String paymentTransactionId = "Not available";
        if (!acquirerDataJson.isNull("rrn"))
            return acquirerDataJson.getString("rrn");
        if (!acquirerDataJson.isNull("arn"))
            return acquirerDataJson.getString("arn");
        return paymentTransactionId;
    }

    public static boolean isAbnormalBehaviour(Date currentTime, Date lastModifiedAt) {
        if (lastModifiedAt == null) return false;
        long timeDifference = currentTime.getTime() - lastModifiedAt.getTime();
        long secondsDifference = timeDifference / 1000;
        return secondsDifference >= BusinessConfigurations.MAXIMUM_TOLERABLE_ABNORMAL_ACTIVITY_SECONDS;
    }

    public static boolean isBuyerRefundDue(Date currentTime, Date lastPaidAt) {
        long timeDifference = currentTime.getTime() - lastPaidAt.getTime();
        long hoursDifference = timeDifference / (60 * 60 * 1000);
        return hoursDifference >= BusinessConfigurations.MAXIMUM_BUYER_REFUND_DUE_HOURS;
    }

    public static BigDecimal getInrFromPaise(int paise) {
        return BigDecimal.valueOf(paise / BusinessConfigurations.UNIT_INR_IN_PAISE)
                .setScale(2, RoundingMode.CEILING);
    }

    public static int getPaiseFromInr(BigDecimal ruppee) {
        return ruppee.
                multiply(BigDecimal.valueOf(100))
                .intValue();
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
