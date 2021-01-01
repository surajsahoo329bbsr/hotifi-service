package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.payment.web.request.PurchaseRequest;
import com.api.hotifi.payment.web.responses.PurchaseReceiptResponse;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.WifiSummaryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface IPurchaseService {

    @Transactional(readOnly = true)
    boolean isCurrentSessionLegit(Long buyerId, Long sessionId, int dataToBeUsed);

    PurchaseReceiptResponse addPurchase(PurchaseRequest purchaseRequest);

    PurchaseReceiptResponse getPurchaseReceipt(Long purchaseId);

    Date startBuyerWifiService(Long purchaseId, int status);

    RefundReceiptResponse withdrawBuyerRefunds(Long buyerId);

    /*
        Below method returns the following codes
            0 if successfully updated
            1 if 90% data is consumed
            2 if buyer's wifi service is to be stopped
            -1 if exception occurs
    */
    int updateBuyerWifiService(Long purchaseId, int status, double dataUsed);

    WifiSummaryResponse finishBuyerWifiService(Long purchaseId, int status, double dataUsed);

    List<WifiSummaryResponse> getSortedWifiUsagesDateTime(Long buyerId, int page, int size, boolean isDescending);

    List<WifiSummaryResponse> getSortedWifiUsagesDataUsed(Long buyerId, int page, int size, boolean isDescending);

    @Transactional(readOnly = true)
    List<RefundReceiptResponse> getBuyerRefundReceipts(Long buyerId, int page, int size, boolean isDescending);
}