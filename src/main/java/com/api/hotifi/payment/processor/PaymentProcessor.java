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

import java.util.Date;

@Getter
@Setter
@Slf4j
public class PaymentProcessor {

    private PaymentMethodCodes paymentMethodCodes;

    private UpiPaymentCodes upiPaymentCodes;

    private PaymentGatewayCodes paymentGatewayCodes;

    public PaymentProcessor(PaymentGatewayCodes paymentGatewayCodes){
        this.paymentGatewayCodes = paymentGatewayCodes;
    }

    public PaymentProcessor(PaymentGatewayCodes paymentGatewayCodes, PaymentMethodCodes paymentMethodCodes){
        this.paymentGatewayCodes = paymentGatewayCodes;
        this.paymentMethodCodes = paymentMethodCodes;
    }

    public PaymentProcessor(PaymentGatewayCodes paymentGatewayCodes, PaymentMethodCodes paymentMethodCodes, UpiPaymentCodes upiPaymentCodes){
        this.paymentGatewayCodes = paymentGatewayCodes;
        this.paymentMethodCodes = paymentMethodCodes;
        this.upiPaymentCodes = upiPaymentCodes;
    }

    //TODO given methods are for test purpose only. Add respective payment gateway codes...
    public boolean isUpiIdValid(String upiId){
        //TODO add UpiId verification codes
        switch (paymentGatewayCodes){
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

    public Purchase getBuyerPurchase(String paymentId){
        switch (paymentGatewayCodes){
            case RAZORPAY:
                log.info("TODO RAZORAPAY PAYMENT");
                Purchase purchase = new Purchase();
                purchase.setStatus((paymentMethodCodes.value() * Constants.BUYER_PAYMENT_START_VALUE_CODE) + upiPaymentCodes.value());
                purchase.setPaymentId("123456790");
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

    public RefundReceiptResponse getBuyerPaymentStatus(String paymentId){
        switch (paymentGatewayCodes){
            case RAZORPAY:
                Purchase purchase = new Purchase();
                purchase.setStatus(404);
                log.info("TODO RAZORAPAY PAYMENT");
                return new RefundReceiptResponse(purchase,"1234", new Date(System.currentTimeMillis()), Constants.HOTIFI_UPI_ID, null);
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public SellerReceiptResponse getSellerPaymentStatus(String paymentId){
        switch (paymentGatewayCodes){
            case RAZORPAY:
                log.info("TODO RAZORAPAY PAYMENT");
                SellerReceipt sellerReceipt = new SellerReceipt();
                sellerReceipt.setStatus(404);
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

    public RefundReceiptResponse startBuyerRefund(double refundAmount, String upiId, String email){
        switch (paymentGatewayCodes){
            case RAZORPAY:
                Purchase purchase = new Purchase();
                purchase.setStatus(404);
                log.info("TODO RAZORAPAY PAYMENT");
                return new RefundReceiptResponse(purchase,"1234", new Date(System.currentTimeMillis()), Constants.HOTIFI_UPI_ID, null);
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public SellerReceiptResponse startSellerPayment(double refundAmount, String upiId, String email){
        switch (paymentGatewayCodes){
            case RAZORPAY:
                log.info("TODO RAZORAPAY PAYMENT");
                SellerReceipt sellerReceipt = new SellerReceipt();
                sellerReceipt.setStatus(404);
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
