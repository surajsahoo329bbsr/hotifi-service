package com.api.hotifi.payment.processor.razorpay;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.payment.error.RazorpayErrorCodes;
import com.razorpay.*;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class RazorpayProcessor {

    RazorpayClient razorpayClient;

    public RazorpayProcessor() {
        try {
            razorpayClient = new RazorpayClient(Constants.RAZORPAY_CLIENT_ID, Constants.RAZORPAY_CLIENT_SECRET);
        } catch (RazorpayException e) {
            throw new HotifiException(RazorpayErrorCodes.INVALID_CLIENT_CREDENTIALS);
        }
    }

    //Below methods have json response body above them in comments

    //To be used later if required for now auto-capture will be used

    /**
     * {
     * "id": "pay_G8VQzjPLoAvm6D",
     * "entity": "payment",
     * "amount": 1000,
     * "currency": "INR",
     * "status": "captured",
     * "order_id": "order_G8VPOayFxWEU28",
     * "invoice_id": null,
     * "international": false,
     * "method": "upi",
     * "amount_refunded": 0,
     * "refund_status": null,
     * "captured": true,
     * "description": "Purchase Shoes",
     * "card_id": null,
     * "bank": null,
     * "wallet": null,
     * "vpa": "gaurav.kumar@exampleupi",
     * "email": "gaurav.kumar@example.com",
     * "contact": "+919999999999",
     * "customer_id": "cust_DitrYCFtCIokBO",
     * "notes": [],
     * "fee": 24,
     * "tax": 4,
     * "error_code": null,
     * "error_description": null,
     * "error_source": null,
     * "error_step": null,
     * "error_reason": null,
     * "acquirer_data": {
     * "rrn": "033814379298"
     * },
     * "created_at": 1606985209
     * }
     */
    public void capturePaymentById(String paymentId, int amountInPaise, String currency) {
        try {
            JSONObject captureRequest = new JSONObject();
            captureRequest.put("amount", amountInPaise);
            captureRequest.put("currency", currency);
            razorpayClient.Payments.capture(paymentId, captureRequest);
        } catch (RazorpayException e) {
            throw new HotifiException(RazorpayErrorCodes.CAPTURE_PAYMENT_FAILED);
        }
    }

    /**
     * {
     * "id": "pay_G8VQzjPLoAvm6D",
     * "entity": "payment",
     * "amount": 1000,
     * "currency": "INR",
     * "status": "captured",
     * "order_id": "order_G8VPOayFxWEU28",
     * "invoice_id": null,
     * "international": false,
     * "method": "upi",
     * "amount_refunded": 0,
     * "refund_status": null,
     * "captured": true,
     * "description": "Purchase Shoes",
     * "card_id": null,
     * "bank": null,
     * "wallet": null,
     * "vpa": "gaurav.kumar@exampleupi",
     * "email": "gaurav.kumar@example.com",
     * "contact": "+919999999999",
     * "customer_id": "cust_DitrYCFtCIokBO",
     * "notes": [],
     * "fee": 24,
     * "tax": 4,
     * "error_code": null,
     * "error_description": null,
     * "error_source": null,
     * "error_step": null,
     * "error_reason": null,
     * "acquirer_data": {
     * "rrn": "033814379298"
     * },
     * "created_at": 1606985209
     * }
     */
    public Payment getPaymentById(String paymentId) {
        try {
            return razorpayClient.Payments.fetch(paymentId);
        } catch (RazorpayException e) {
            throw new HotifiException(RazorpayErrorCodes.PAYMENT_NOT_FOUND);
        }
    }

    /**
     * {
     * "entity": "collection",
     * "count": 2,
     * "items": [
     * {
     * "id": "pay_E9uth3WhYbh9QV",
     * "entity": "payment",
     * "amount": 100,
     * "currency": "INR",
     * "status": "captured",
     * "order_id": null,
     * "invoice_id": null,
     * "international": null,
     * "method": "transfer",
     * "amount_refunded": 0,
     * "refund_status": null,
     * "captured": true,
     * "description": null,
     * "card_id": null,
     * "bank": null,
     * "wallet": null,
     * "vpa": null,
     * "email": "",
     * "contact": null,
     * "notes": [],
     * "fee": 0,
     * "tax": 0,
     * "error_code": null,
     * "error_description": null,
     * "created_at": 1580219046
     * },
     * {
     * "id": "pay_E9ulvYl52I27fB",
     * "entity": "payment",
     * "amount": 100,
     * "currency": "INR",
     * "status": "captured",
     * "order_id": null,
     * "invoice_id": null,
     * "international": null,
     * "method": "transfer",
     * "amount_refunded": 0,
     * "refund_status": null,
     * "captured": true,
     * "description": null,
     * "card_id": null,
     * "bank": null,
     * "wallet": null,
     * "vpa": null,
     * "email": "gaurav.kumar@example.com",
     * "contact": "+918888888888",
     * "notes": {
     * "roll_no": "IEC2011025"
     * },
     * "fee": 0,
     * "tax": 0,
     * "error_code": null,
     * "error_description": null,
     * "created_at": 1580218605
     * }
     * ]
     * }
     */
    public List<Payment> getPaymentsByLinkedAccount(String linkedAccountId) {
        try {
            razorpayClient.addHeaders(Map.of("X-Razorpay-Account", linkedAccountId));
            return razorpayClient.Payments.fetchAll();
        } catch (RazorpayException e) {
            throw new HotifiException(RazorpayErrorCodes.PAYMENTS_OF_LINKED_ACCOUNT_NOT_FOUND);
        }
    }

    /**
     * {
     * "id": "rfnd_FP8QHiV938haTz",
     * "entity": "refund",
     * "amount": 500100,
     * "currency": "INR",
     * "payment_id": "pay_FCXKPFtYfPXJPy",
     * "notes": []
     * "receipt": null,
     * "acquirer_data": {
     * "arn": null
     * },
     * "created_at": 1597078866,
     * "batch_id": null,
     * "status": "processed",
     * "speed_processed": "normal",
     * "speed_requested": "normal"
     * }
     */
    public Refund startNormalFullRefund(String paymentId) {
        try {
            return razorpayClient.Payments.refund(paymentId);
        } catch (RazorpayException e) {
            throw new HotifiException(RazorpayErrorCodes.NORMAL_FULL_REFUND_FAILED);
        }
    }

    /**
     * {
     * "id": "rfnd_FP8QHiV938haTz",
     * "entity": "refund",
     * "amount": 500100,
     * "currency": "INR",
     * "payment_id": "pay_FCXKPFtYfPXJPy",
     * "notes": []
     * "receipt": null,
     * "acquirer_data": {
     * "arn": null
     * },
     * "created_at": 1597078866,
     * "batch_id": null,
     * "status": "processed",
     * "speed_processed": "normal",
     * "speed_requested": "normal"
     * }
     */
    public Refund startNormalPartialRefund(String paymentId, int amountInPaise) {
        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", amountInPaise); // Amount should be in paise
        try {
            return razorpayClient.Payments.refund(paymentId, refundRequest);
        } catch (RazorpayException e) {
            throw new HotifiException(RazorpayErrorCodes.NORMAL_PARTIAL_REFUND_FAILED);
        }
    }

    /**
     * {
     * "id": "rfnd_FP8DDKxqJif6ca",
     * "entity": "refund",
     * "amount": 300100,
     * "currency": "INR",
     * "payment_id": "pay_FIKOnlyii5QGNx",
     * "notes": {
     * "comment": "Comment for refund"
     * },
     * "receipt": null,
     * "acquirer_data": {
     * "arn": "10000000000000"
     * },
     * "created_at": 1597078124,
     * "batch_id": null,
     * "status": "processed",
     * "speed_processed": "normal",
     * "speed_requested": "optimum"
     * }
     */
    public Refund getSpecificRefundById(String paymentId, String refundId) {
        try {
            return razorpayClient.Payments.fetchRefund(paymentId, refundId);
        } catch (RazorpayException e) {
            throw new HotifiException(RazorpayErrorCodes.SPECIFIC_REFUND_NOT_FOUND);
        }
    }

    /**
     * {
     * "id": "rfnd_EqWThTE7dd7utf",
     * "entity": "refund",
     * "amount": 6000,
     * "currency": "INR",
     * "payment_id": "pay_EpkFDYRirena0f",
     * "notes": {
     * "comment": "Issuing an instant refund"
     * },
     * "receipt": null,
     * "acquirer_data": {
     * "arn": "10000000000000"
     * },
     * "created_at": 1589521675,
     * "batch_id": null,
     * "status": "processed",
     * "speed_processed": "optimum",
     * "speed_requested": "optimum"
     * }
     */
    public Refund getRefundById(String refundId) {
        try {
            return razorpayClient.Refunds.fetch(refundId);
        } catch (RazorpayException e) {
            throw new HotifiException(RazorpayErrorCodes.REFUND_NOT_FOUND);
        }
    }

    /**
     * {
     * "id": "trf_E9utgtfGTcpcmm",
     * "entity": "transfer",
     * "source": "acc_CJoeHMNpi0nC7k",
     * "recipient": "acc_CPRsN1LkFccllA",
     * "amount": 100,
     * "currency": "INR",
     * "amount_reversed": 0,
     * "notes": [],
     * "fees": 1,
     * "tax": 0,
     * "on_hold": false,
     * "on_hold_until": null,
     * "recipient_settlement_id": null,
     * "created_at": 1580219046,
     * "linked_account_notes": [],
     * "processed_at": 1580219046
     * }
     */
    public Transfer startTransfer(String linkedAccountId, int amountInPaise, String currency) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", linkedAccountId);
        jsonObject.put("amount", amountInPaise);
        jsonObject.put("currency", currency);
        try {
            return razorpayClient.Transfers.create(jsonObject);
        } catch (RazorpayException e) {
            throw new HotifiException(RazorpayErrorCodes.TRANSFER_FAILED);
        }
    }

    /**
     * {
     * "id": "trf_E7V62rAxJ3zYMo",
     * "entity": "transfer",
     * "source": "pay_E6j30Iu1R7XbIG",
     * "recipient": "acc_CMaomTz4o0FOFz",
     * "amount": 100,
     * "currency": "INR",
     * "amount_reversed": 0,
     * "notes": [],
     * "fees": 1,
     * "tax": 0,
     * "on_hold": false,
     * "on_hold_until": null,
     * "recipient_settlement_id": null,
     * "created_at": 1579691505,
     * "linked_account_notes": [],
     * "processed_at": 1579691505
     * }
     */
    public Transfer getTransferById(String transferId) {
        try {
            return razorpayClient.Transfers.fetch(transferId);
        } catch (RazorpayException e) {
            throw new HotifiException(RazorpayErrorCodes.TRANSFER_NOT_FOUND);
        }
    }
}
