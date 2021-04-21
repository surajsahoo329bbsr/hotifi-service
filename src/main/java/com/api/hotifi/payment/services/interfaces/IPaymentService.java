package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.web.responses.PendingMoneyResponse;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;

import java.math.BigDecimal;
import java.util.List;

public interface IPaymentService {

    //DO NOT ADD TO CONTROLLER
    void addSellerPayment(User seller, BigDecimal amountEarned);

    //DO NOT ADD TO CONTROLLER
    void updateSellerPayment(User seller, BigDecimal amountEarned, boolean isUpdateTimeOnly);

    //DO NOT ADD TO CONTROLLER
    SellerReceiptResponse addSellerReceipt(User seller, SellerPayment sellerPayment, BigDecimal sellerAmountPaid);

    SellerReceiptResponse getSellerReceipt(Long id);

    List<SellerReceiptResponse> getSortedSellerReceiptsByDateTime(Long sellerPaymentId, int page, int size, boolean isDescending);

    List<SellerReceiptResponse> getSortedSellerReceiptsByAmountPaid(Long sellerPaymentId, int page, int size, boolean isDescending);

    void withdrawBuyerRefunds(Long buyerId);

    List<RefundReceiptResponse> getBuyerRefundReceipts(Long buyerId, int page, int size, boolean isDescending);

    SellerReceiptResponse withdrawSellerPayment(Long sellerId);

    PendingMoneyResponse getPendingPayments(Long sellerId);
}
