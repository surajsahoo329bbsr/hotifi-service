package com.api.hotifi.payment.processor;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerReceipt;
import com.api.hotifi.payment.processor.codes.BuyerPaymentCodes;
import com.api.hotifi.payment.processor.codes.PaymentGatewayCodes;
import com.api.hotifi.payment.processor.codes.PaymentMethodCodes;
import com.api.hotifi.payment.processor.codes.SellerPaymentCodes;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.repositories.SellerReceiptRepository;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Slf4j
public class PaymentProcessor {

    private PaymentMethodCodes paymentMethodCodes;

    private PaymentGatewayCodes paymentGatewayCodes;

    private List<Integer> buyerPaymentCodesList = new ArrayList<>(Arrays.asList(
            100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
            200, 201, 202, 203, 204, 205, 206, 207, 208, 209,
            300, 301, 302, 303, 304, 305, 306, 307, 308, 309,
            400, 401, 402, 403, 404, 405, 406, 407, 408, 409,
            500, 501, 502, 503, 504, 505, 506, 507, 508, 509));

    /*private List<Integer> buyerRefundCodesList = new ArrayList<>(Arrays.asList(
            107, 108, 109,
            207, 208, 209,
            307, 308, 309,
            407, 408, 409));*/

    private List<Integer> sellerPaymentCodesList = new ArrayList<>(Arrays.asList(
            1000, 1001, 1002, 1003,
            2000, 2001, 2002, 2003,
            3000, 3001, 3002, 3003,
            4000, 4001, 4002, 4003,
            5000, 5001, 5002, 5003));

    public PaymentProcessor(PaymentGatewayCodes paymentGatewayCodes) {
        this.paymentGatewayCodes = paymentGatewayCodes;
    }

    public PaymentProcessor(PaymentGatewayCodes paymentGatewayCodes, PaymentMethodCodes paymentMethodCodes) {
        this.paymentGatewayCodes = paymentGatewayCodes;
        this.paymentMethodCodes = paymentMethodCodes;
    }

    //TODO given methods are for test purpose only. Add respective payment gateway codes...
    public boolean isLinkedAccountIdValid(String linkedAccountId) {
        //TODO add UpiId verification codes
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORPAY PAYMENT");
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
                log.info("TODO RAZORPAY PAYMENT");
                Purchase purchase = new Purchase();
                //TODO add status, for testing all payments are successful
                Date now = new Date(System.currentTimeMillis());
                purchase.setStatus(paymentMethodCodes.value() + BuyerPaymentCodes.PAYMENT_SUCCESSFUL.value());
                purchase.setPaymentId(paymentId + now);
                purchase.setPaymentDoneAt(now);
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

    public RefundReceiptResponse getBuyerPaymentStatus(PurchaseRepository purchaseRepository, String paymentId, boolean isRefundToBeStarted) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORPAY PAYMENT");
                Purchase purchase = purchaseRepository.findByPaymentId(paymentId);
                if (isRefundToBeStarted)
                    purchase.setStatus(purchase.getStatus() - purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE + BuyerPaymentCodes.REFUND_STARTED.value());
                else
                    purchase.setStatus(purchase.getStatus() - purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE + BuyerPaymentCodes.REFUND_SUCCESSFUL.value());
                return new RefundReceiptResponse(purchase, Constants.HOTIFI_BANK_ACCOUNT);
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public SellerReceiptResponse getSellerPaymentStatus(SellerReceiptRepository sellerReceiptRepository, String paymentId) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORPAY PAYMENT");
                SellerReceipt sellerReceipt = sellerReceiptRepository.findByPaymentId(paymentId);
                sellerReceipt.setStatus(SellerPaymentCodes.PAYMENT_SUCCESSFUL.value());
                SellerReceiptResponse sellerReceiptResponse = new SellerReceiptResponse();
                sellerReceiptResponse.setSellerReceipt(sellerReceipt);
                sellerReceiptResponse.setHotifiBankAccount(Constants.HOTIFI_BANK_ACCOUNT);
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

    public RefundReceiptResponse startBuyerRefund(PurchaseRepository purchaseRepository, BigDecimal refundAmount, String paymentId, String email) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORPAY PAYMENT");
                Purchase purchase = purchaseRepository.findByPaymentId(paymentId);
                Date refundStartedAt = new Date(System.currentTimeMillis());
                purchase.setRefundStartedAt(refundStartedAt);
                purchase.setStatus(purchase.getStatus() - purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE + BuyerPaymentCodes.REFUND_STARTED.value());
                return new RefundReceiptResponse(purchase, Constants.HOTIFI_BANK_ACCOUNT);
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public SellerReceiptResponse startSellerPayment(BigDecimal sellerPendingAmount, String linkedAccountId, String email) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORPAY PAYMENT");
                SellerReceipt sellerReceipt = new SellerReceipt();
                Date now = new Date(System.currentTimeMillis());
                //TODO transfer money to seller's account using razorpay's linkedAccountId
                sellerReceipt.setStatus(SellerPaymentCodes.PAYMENT_STARTED.value());
                sellerReceipt.setCreatedAt(now);
                sellerReceipt.setAmountPaid(sellerPendingAmount);
                sellerReceipt.setPaymentId("pay_1234" + now);
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
