package com.api.hotifi.payment.processor;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerReceipt;
import com.api.hotifi.payment.processor.codes.BuyerPaymentCodes;
import com.api.hotifi.payment.processor.codes.PaymentGatewayCodes;
import com.api.hotifi.payment.processor.codes.PaymentMethodCodes;
import com.api.hotifi.payment.processor.codes.SellerPaymentCodes;
import com.api.hotifi.payment.processor.razorpay.RazorpayProcessor;
import com.api.hotifi.payment.processor.razorpay.codes.PaymentStatusCodes;
import com.api.hotifi.payment.processor.razorpay.codes.RefundStatusCodes;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.repositories.SellerReceiptRepository;
import com.api.hotifi.payment.utils.PaymentUtils;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import com.razorpay.Payment;
import com.razorpay.Refund;
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

    private RazorpayProcessor razorpayProcessor;

    public PaymentProcessor(PaymentGatewayCodes paymentGatewayCodes) {
        this.paymentGatewayCodes = paymentGatewayCodes;
        razorpayProcessor = new RazorpayProcessor();
    }

    public PaymentProcessor(PaymentGatewayCodes paymentGatewayCodes, PaymentMethodCodes paymentMethodCodes) {
        this.paymentGatewayCodes = paymentGatewayCodes;
        this.paymentMethodCodes = paymentMethodCodes;
        razorpayProcessor = new RazorpayProcessor();
    }

    public Purchase createBuyerPurchase(String paymentId, BigDecimal amountPaid) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                Payment payment = razorpayProcessor.getPaymentById(paymentId);
                Date paymentDoneAt = new Date((long) payment.get("created_at"));
                PaymentMethodCodes paymentMethod = PaymentMethodCodes.valueOf(payment.get("method").toString().toUpperCase());
                PaymentStatusCodes razorpayStatus = PaymentStatusCodes.valueOf(payment.get("status").toString().toUpperCase());
                int amountPaidInPaise = PaymentUtils.getPaiseFromInr(amountPaid);
                if (razorpayStatus == PaymentStatusCodes.REFUNDED) return null;
                //If auto-captured failed do the manual capture
                if (razorpayStatus == PaymentStatusCodes.AUTHORIZED)
                    razorpayProcessor.capturePaymentById(paymentId, amountPaidInPaise, "INR");
                //Purchase entity model update
                Purchase purchase = new Purchase();
                purchase.setStatus(paymentMethod.value() + razorpayStatus.value());
                purchase.setPaymentId(paymentId);
                purchase.setPaymentDoneAt(paymentDoneAt);
                return purchase;
            case STRIPE:
                log.info("STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public RefundReceiptResponse getBuyerPaymentStatus(PurchaseRepository purchaseRepository, String paymentId, boolean isRefundToBeStarted) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("RAZORPAY PAYMENT");
                Purchase purchase = purchaseRepository.findByPaymentId(paymentId);
                if (isRefundToBeStarted)
                    return startBuyerRefund(purchaseRepository, purchase.getAmountRefund(), paymentId);

                Refund refund = razorpayProcessor.getRefundById(purchase.getRefundPaymentId());
                String refundId = refund.get("id");
                Date refundCreatedAt = new Date((long) refund.get("created_at"));
                RefundStatusCodes refundStatus = RefundStatusCodes.valueOf(refund.get("status").toString().toUpperCase());
                int buyerPaymentStatus = BuyerPaymentCodes.values().length - refundStatus.value();
                //modify purchase entity
                purchase.setRefundPaymentId(refundId);
                purchase.setRefundDoneAt(refundCreatedAt);
                purchase.setStatus(purchase.getStatus() - purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE + buyerPaymentStatus);
                return new RefundReceiptResponse(purchase, Constants.HOTIFI_BANK_ACCOUNT);
            case STRIPE:
                log.info("STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public SellerReceiptResponse getSellerPaymentStatus(SellerReceiptRepository sellerReceiptRepository, String paymentId) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("RAZORPAY PAYMENT");
                SellerReceipt sellerReceipt = sellerReceiptRepository.findByPaymentId(paymentId);
                sellerReceipt.setStatus(SellerPaymentCodes.PAYMENT_SUCCESSFUL.value());
                SellerReceiptResponse sellerReceiptResponse = new SellerReceiptResponse();
                sellerReceiptResponse.setSellerReceipt(sellerReceipt);
                sellerReceiptResponse.setHotifiBankAccount(Constants.HOTIFI_BANK_ACCOUNT);
                return sellerReceiptResponse;
            case STRIPE:
                log.info("STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public RefundReceiptResponse startBuyerRefund(PurchaseRepository purchaseRepository, BigDecimal refundAmount, String paymentId) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORPAY PAYMENT");
                Purchase purchase = purchaseRepository.findByPaymentId(paymentId);
                int amountInPaise = PaymentUtils.getPaiseFromInr(refundAmount);
                //Creating refund entity below
                Refund refund = razorpayProcessor.startNormalPartialRefund(paymentId, amountInPaise);
                String refundId = refund.get("id");
                Date refundStartedAt = new Date((long) refund.get("created_at"));
                RefundStatusCodes refundStatus = RefundStatusCodes.valueOf(refund.get("status").toString().toUpperCase());
                int buyerPaymentStatus = BuyerPaymentCodes.values().length - refundStatus.value();
                //Purchase entity setup
                purchase.setRefundPaymentId(refundId);
                purchase.setRefundStartedAt(refundStartedAt);
                purchase.setStatus(purchase.getStatus() - purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE + buyerPaymentStatus);
                return new RefundReceiptResponse(purchase, Constants.HOTIFI_BANK_ACCOUNT);
            case STRIPE:
                log.info("STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("PAYPAL PAYMENT");
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
