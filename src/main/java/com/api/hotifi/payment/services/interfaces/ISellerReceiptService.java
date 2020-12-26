package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;

import java.util.List;

public interface ISellerReceiptService {

    SellerReceiptResponse addSellerReceipt(User seller, SellerPayment sellerPayment, double sellerAmountPaid);

    SellerReceiptResponse getSellerReceipt(Long id);

    List<SellerReceiptResponse> getSortedSellerReceiptsByDateTime(Long sellerPaymentId, int pageNumber, int elements, boolean isDescending);

    List<SellerReceiptResponse> getSortedSellerReceiptsByAmountPaid(Long sellerPaymentId, int pageNumber, int elements, boolean isDescending);

}
