package com.api.hotifi.payment.processor;

import com.api.hotifi.payment.processor.codes.PaymentGatewayCodes;
import com.api.hotifi.payment.processor.codes.PaymentMethodCodes;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Builder
@Slf4j
public class PaymentProcessor {

    private PaymentMethodCodes paymentMethodCodes;

    private PaymentGatewayCodes paymentGatewayCodes;

    public PaymentProcessor(PaymentMethodCodes paymentMethodCodes, PaymentGatewayCodes paymentGatewayCodes){
        this.paymentMethodCodes = paymentMethodCodes;
        this.paymentGatewayCodes = paymentGatewayCodes;
    }

    public boolean isUpiIdValid(String upiId){
        //TODO add UpiId verification codes
        return true;
    }

    public RefundReceiptResponse getBuyerPaymentStatus(String paymentId){
        switch (paymentGatewayCodes){
            case RAZORPAY:
                log.info("TODO RAZORAPAY PAYMENT");
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
                break;
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
                log.info("TODO RAZORAPAY PAYMENT");
                break;
            case STRIPE:
                log.info("TODO STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("TODO PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public SellerReceiptResponse startSellerPaymnet(double refundAmount, String upiId, String email){
        switch (paymentGatewayCodes){
            case RAZORPAY:
                break;
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
