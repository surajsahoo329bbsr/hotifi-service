package com.api.hotifi.payment.service;

import com.api.hotifi.payment.web.request.PurchaseRequest;
import com.api.hotifi.payment.web.response.PurchaseReceiptResponse;
import com.api.hotifi.payment.web.response.WifiSummaryResponse;

import java.util.Date;
import java.util.List;

public interface IPurchaseService {

    PurchaseReceiptResponse addPurchase(PurchaseRequest purchaseRequest);

    PurchaseReceiptResponse getPurchaseReceipt(Long purchaseId);

    Date startBuyerWifiService(Long purchaseId, int status);

    /*
        Below method returns the following codes
            0 if successfully updated
            1 if 90% data is consumed
            2 if buyer's wifi service is to be stopped
            -1 if exception occurs
    */
    int updateBuyerDataUsed(Long purchaseId, int status, double dataUsed);

    WifiSummaryResponse endBuyerWifiService(Long purchaseId, int status, double dataUsed);

    List<WifiSummaryResponse> getSortedWifiUsagesDateTime(Long buyerId, int pageNumber, int elements, boolean isDescending);

    List<WifiSummaryResponse> getSortedWifiUsagesDataUsed(Long buyerId, int pageNumber, int elements, boolean isDescending);
}