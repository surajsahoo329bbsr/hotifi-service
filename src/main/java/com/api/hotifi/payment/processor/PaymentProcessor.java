package com.api.hotifi.payment.processor;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerReceipt;
import com.api.hotifi.payment.processor.codes.PaymentGatewayCodes;
import com.api.hotifi.payment.processor.codes.PaymentMethodCodes;
import com.api.hotifi.payment.processor.codes.UpiPaymentCodes;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Getter
@Setter
@Slf4j
public class PaymentProcessor {

    private PaymentMethodCodes paymentMethodCodes;

    private UpiPaymentCodes upiPaymentCodes;

    private PaymentGatewayCodes paymentGatewayCodes;

    private List<Integer> buyerPaymentCodesList = new ArrayList<>(Arrays.asList(
            100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
            200, 201, 202, 203, 204, 205, 206, 207, 208, 209,
            300, 301, 302, 303, 304, 305, 306, 307, 308, 309,
            400, 401, 402, 403, 404, 405, 406, 407, 408, 409));

    /*private List<Integer> buyerRefundCodesList = new ArrayList<>(Arrays.asList(
            107, 108, 109,
            207, 208, 209,
            307, 308, 309,
            407, 408, 409));*/

    private List<Integer> sellerPaymentCodesList = new ArrayList<>(Arrays.asList(
            1000, 1001, 1002, 1003,
            2000, 2001, 2002, 2003,
            3000, 3001, 3002, 3003,
            4000, 4001, 4002, 4003));

    public PaymentProcessor(PaymentGatewayCodes paymentGatewayCodes) {
        this.paymentGatewayCodes = paymentGatewayCodes;
    }

    public PaymentProcessor(PaymentGatewayCodes paymentGatewayCodes, PaymentMethodCodes paymentMethodCodes) {
        this.paymentGatewayCodes = paymentGatewayCodes;
        this.paymentMethodCodes = paymentMethodCodes;
    }

    public PaymentProcessor(PaymentGatewayCodes paymentGatewayCodes, PaymentMethodCodes paymentMethodCodes, UpiPaymentCodes upiPaymentCodes) {
        this.paymentGatewayCodes = paymentGatewayCodes;
        this.paymentMethodCodes = paymentMethodCodes;
        this.upiPaymentCodes = upiPaymentCodes;
    }

    //TODO given methods are for test purpose only. Add respective payment gateway codes...
    public boolean isUpiIdValid(String upiId) {
        //TODO add UpiId verification codes
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORAPAY PAYMENT");
                return true;
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return false;
    }

    public Purchase getBuyerPurchase(String paymentId) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORAPAY PAYMENT");
                Purchase purchase = new Purchase();
                purchase.setStatus(upiPaymentCodes.value() * Constants.BUYER_PAYMENT_START_VALUE_CODE);
                purchase.setPaymentId(paymentId);
                purchase.setPaymentDoneAt(new Date(System.currentTimeMillis()));
                return purchase;
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public RefundReceiptResponse getBuyerPaymentStatus(String paymentId) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORAPAY PAYMENT");
                Purchase purchase = new Purchase();
                int index = new Random().nextInt(buyerPaymentCodesList.size());
                purchase.setStatus(buyerPaymentCodesList.get(index));
                log.info("status : " + buyerPaymentCodesList.get(index));
                return new RefundReceiptResponse(purchase, "ref_4512", new Date(System.currentTimeMillis()), Constants.HOTIFI_UPI_ID, null);
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public SellerReceiptResponse getSellerPaymentStatus(String paymentId) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORAPAY PAYMENT");
                SellerReceipt sellerReceipt = new SellerReceipt();
                int index = new Random().nextInt(sellerPaymentCodesList.size());
                sellerReceipt.setStatus(sellerPaymentCodesList.get(index));
                log.info("status : " + sellerPaymentCodesList.get(index));
                SellerReceiptResponse sellerReceiptResponse = new SellerReceiptResponse();
                sellerReceiptResponse.setSellerReceipt(sellerReceipt);
                return sellerReceiptResponse;
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public RefundReceiptResponse startBuyerRefund(double refundAmount, String upiId, String email) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORAPAY PAYMENT");
                Purchase purchase = new Purchase();
                int index = new Random().nextInt(buyerPaymentCodesList.size());
                purchase.setStatus(buyerPaymentCodesList.get(index));
                log.info("status : " + buyerPaymentCodesList.get(index));
                return new RefundReceiptResponse(purchase, "ref_1234", new Date(System.currentTimeMillis()), Constants.HOTIFI_UPI_ID, null);
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public SellerReceiptResponse startSellerPayment(double refundAmount, String upiId, String email) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORAPAY PAYMENT");
                SellerReceipt sellerReceipt = new SellerReceipt();
                int index = new Random().nextInt(sellerPaymentCodesList.size());
                sellerReceipt.setStatus(sellerPaymentCodesList.get(index));
                SellerReceiptResponse sellerReceiptResponse = new SellerReceiptResponse();
                sellerReceiptResponse.setSellerReceipt(sellerReceipt);
                return sellerReceiptResponse;
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return null;
    }

}
