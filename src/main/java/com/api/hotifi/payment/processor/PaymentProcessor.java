package com.api.hotifi.payment.processor;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerReceipt;
import com.api.hotifi.payment.processor.codes.BuyerPaymentCodes;
import com.api.hotifi.payment.processor.codes.PaymentGatewayCodes;
import com.api.hotifi.payment.processor.codes.PaymentMethodCodes;
import com.api.hotifi.payment.processor.codes.SellerPaymentCodes;
import com.api.hotifi.payment.processor.razorpay.RazorpayProcessor;
import com.api.hotifi.payment.processor.razorpay.codes.PaymentStatusCodes;
import com.api.hotifi.payment.processor.razorpay.codes.RefundStatusCodes;
import com.api.hotifi.payment.processor.response.Settlement;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.repositories.SellerReceiptRepository;
import com.api.hotifi.payment.utils.PaymentUtils;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import com.razorpay.Payment;
import com.razorpay.Refund;
import com.razorpay.Transfer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;

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

    public void capturePayment(String paymentId, int amountInr) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                int amount = amountInr * BusinessConfigurations.UNIT_INR_IN_PAISE;
                razorpayProcessor.capturePaymentById(paymentId, amount, "INR");
            case STRIPE:
                log.info("STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("PAYPAL PAYMENT");
                break;
        }

    }

    public Purchase getBuyerPurchase(String paymentId, BigDecimal amountPaid) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                Payment payment = razorpayProcessor.getPaymentById(paymentId);

                Date paymentDoneAt = payment.get("created_at");
                JSONObject acquirerDataJson = payment.get("acquirer_data");
                PaymentMethodCodes paymentMethod = PaymentMethodCodes.valueOf(payment.get("method").toString().toUpperCase());
                PaymentStatusCodes razorpayStatus = PaymentStatusCodes.valueOf(payment.get("status").toString().toUpperCase());

                String paymentTransactionId = PaymentUtils.getPaymentTransactionId(paymentMethod, acquirerDataJson);

                int amountPaidInPaise = PaymentUtils.getPaiseFromInr(amountPaid);
                if (razorpayStatus == PaymentStatusCodes.REFUNDED) return null;
                //If auto-captured failed do the manual capture
                //Purchase entity model update
                Purchase purchase = new Purchase();
                if (razorpayStatus == PaymentStatusCodes.AUTHORIZED) {
                    try {
                        //calling Api To Capture Payment
                        razorpayProcessor.capturePaymentById(paymentId, amountPaidInPaise, BusinessConfigurations.CURRENCY_INR);
                        purchase.setStatus(paymentMethod.value() + PaymentStatusCodes.CAPTURED.value());
                    } catch (Exception e) {
                        e.printStackTrace();
                        purchase.setStatus(paymentMethod.value() + PaymentStatusCodes.AUTHORIZED.value());
                    }
                } else
                    purchase.setStatus(paymentMethod.value() + razorpayStatus.value()); //means either status is created or failed
                purchase.setPaymentId(paymentId);
                purchase.setPaymentDoneAt(paymentDoneAt);
                purchase.setPaymentTransactionId(paymentTransactionId);
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

    public RefundReceiptResponse getBuyerRefundStatus(PurchaseRepository purchaseRepository, String paymentId) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("RAZORPAY PAYMENT");
                Purchase purchase = purchaseRepository.findByPaymentId(paymentId);
                if (purchase.getRefundPaymentId() != null) {
                    Refund refund = razorpayProcessor.getRefundById(purchase.getRefundPaymentId());
                    String refundId = refund.get("id");
                    Date refundCreatedAt = refund.get("created_at");
                    RefundStatusCodes refundStatus = RefundStatusCodes.valueOf(refund.get("status").toString().toUpperCase());
                    JSONObject acquirerDataJson = refund.get("acquirer_data");
                    String refundTransactionId = PaymentUtils.getRefundTransactionId(acquirerDataJson);
                    int buyerPaymentStatus = BuyerPaymentCodes.REFUND_PENDING.value() + refundStatus.value();
                    //modify purchase entity
                    purchase.setStatus((purchase.getStatus() / BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE)
                            * BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE
                            + buyerPaymentStatus);
                    purchase.setRefundDoneAt(refundCreatedAt);
                    purchase.setRefundPaymentId(refundId);
                    purchase.setRefundTransactionId(refundTransactionId);

                }
                return new RefundReceiptResponse(purchase, BusinessConfigurations.HOTIFI_BANK_ACCOUNT);
            case STRIPE:
                log.info("STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public SellerReceiptResponse getSellerPaymentStatus(SellerReceiptRepository sellerReceiptRepository, String transferId) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("RAZORPAY PAYMENT");
                SellerReceipt sellerReceipt = sellerReceiptRepository.findByTransferId(transferId);
                String linkedAccountId = sellerReceipt.getSellerPayment().getSeller().getBankAccount().getLinkedAccountId();
                Transfer transfer = razorpayProcessor.getTransferById(transferId);
                boolean isOnHold = transfer.get("on_hold");
                String settlementId = transfer.get("recipient_settlement_id");

                SellerReceiptResponse sellerReceiptResponse = new SellerReceiptResponse();
                sellerReceipt.setStatus(SellerPaymentCodes.PAYMENT_CREATED.value());
                if (isOnHold) {
                    Date onHoldUntil = transfer.get("on_hold_until");
                    sellerReceiptResponse.setOnHoldUntil(onHoldUntil);
                }
                if (settlementId != null) {
                    Settlement settlement = razorpayProcessor.getSettlementById(settlementId);
                    Date paidAt = PaymentUtils.convertEpochToDate(settlement.getCreatedAt());
                    Date modifiedAt = new Date(System.currentTimeMillis());
                    String transferTransactionId = settlement.getUtr();
                    SellerPaymentCodes sellerPaymentCode = SellerPaymentCodes.valueOf(settlement.getStatus().toUpperCase());
                    sellerReceipt.setPaidAt(paidAt);
                    sellerReceipt.setModifiedAt(modifiedAt);
                    sellerReceipt.setTransferTransactionId(transferTransactionId);
                    sellerReceipt.setStatus(sellerPaymentCode.value());
                }
                //Seller Receipt Response
                sellerReceiptResponse.setOnHold(isOnHold);
                sellerReceiptResponse.setSellerReceipt(sellerReceipt);
                sellerReceiptResponse.setSellerLinkedAccountId(linkedAccountId);
                sellerReceiptResponse.setHotifiBankAccount(BusinessConfigurations.HOTIFI_BANK_ACCOUNT);

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
                BigDecimal zeroAmount = new BigDecimal("0.00");
                boolean isRefundStarted = purchase.getStatus() %
                        BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.REFUND_PENDING.value();

                if (refundAmount.compareTo(zeroAmount) > 0 && !isRefundStarted) {
                    Refund refund = razorpayProcessor.startNormalPartialRefund(paymentId, amountInPaise);
                    String refundId = refund.get("id");
                    Date refundStartedAt = refund.get("created_at");
                    RefundStatusCodes refundStatus = RefundStatusCodes.valueOf(refund.get("status").toString().toUpperCase());
                    int buyerPaymentStatus = BuyerPaymentCodes.REFUND_PENDING.value() + refundStatus.value();
                    //Purchase entity setup
                    purchase.setRefundPaymentId(refundId);
                    purchase.setRefundStartedAt(refundStartedAt);
                    purchase.setStatus((purchase.getStatus() / BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE)
                            * BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE
                            + buyerPaymentStatus);
                }

                return new RefundReceiptResponse(purchase, BusinessConfigurations.HOTIFI_BANK_ACCOUNT);
            case STRIPE:
                log.info("STRIPE PAYMENT");
                break;
            case PAYPAL:
                log.info("PAYPAL PAYMENT");
                break;
        }
        return null;
    }

    public SellerReceiptResponse startSellerPayment(BigDecimal sellerPendingAmount, String linkedAccountId, String bankAccountNumber, String bankIfscCode) {
        switch (paymentGatewayCodes) {
            case RAZORPAY:
                log.info("TODO RAZORPAY PAYMENT");
                Transfer transfer = razorpayProcessor.startTransfer(linkedAccountId, PaymentUtils.getPaiseFromInr(sellerPendingAmount),
                        BusinessConfigurations.CURRENCY_INR);
                SellerReceipt sellerReceipt = new SellerReceipt();
                sellerReceipt.setStatus(SellerPaymentCodes.PAYMENT_CREATED.value());
                Date createdAt = transfer.get("created_at");
                Date modifiedAt = transfer.get("processed_at");
                String transferId = transfer.get("id");
                String settlementId = transfer.get("recipient_settlement_id");
                boolean isOnHold = transfer.get("on_hold");
                Date onHoldUntil = transfer.get("on_hold_until");

                sellerReceipt.setCreatedAt(createdAt);
                sellerReceipt.setModifiedAt(createdAt);
                sellerReceipt.setModifiedAt(modifiedAt);
                sellerReceipt.setTransferId(transferId);
                sellerReceipt.setAmountPaid(sellerPendingAmount);
                sellerReceipt.setBankIfscCode(bankIfscCode);
                sellerReceipt.setBankAccountNumber(bankAccountNumber);
                sellerReceipt.setSettlementId(settlementId);

                //Setup Seller Receipt
                SellerReceiptResponse sellerReceiptResponse = new SellerReceiptResponse();
                sellerReceiptResponse.setHotifiBankAccount(BusinessConfigurations.HOTIFI_BANK_ACCOUNT);
                sellerReceiptResponse.setSellerLinkedAccountId(linkedAccountId);
                sellerReceiptResponse.setOnHold(isOnHold);
                sellerReceiptResponse.setOnHoldUntil(onHoldUntil);

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
