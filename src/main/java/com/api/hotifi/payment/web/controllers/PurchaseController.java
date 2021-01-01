package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.services.interfaces.IPurchaseService;
import com.api.hotifi.payment.web.request.PurchaseRequest;
import com.api.hotifi.payment.web.responses.PurchaseReceiptResponse;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.WifiSummaryResponse;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.DecimalMin;
import java.util.Date;
import java.util.List;

@Validated
@RestController
@Api(tags = Constants.PURCHASE_TAG)
@RequestMapping(path = "/purchase")
public class PurchaseController {

    @Autowired
    private IPurchaseService purchaseService;

    @PostMapping(path = "/add/buyer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPurchase(@RequestBody @Validated PurchaseRequest purchaseRequest) {
        PurchaseReceiptResponse receiptResponse = purchaseService.addPurchase(purchaseRequest);
        return new ResponseEntity<>(receiptResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/get/buyer/receipt/{purchase-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPurchaseReceipt(
            @PathVariable(value = "purchase-id")
            @Range(min = 1, message = "{purchase.id.invalid}") Long purchaseId) {
        PurchaseReceiptResponse receiptResponse = purchaseService.getPurchaseReceipt(purchaseId);
        return new ResponseEntity<>(receiptResponse, HttpStatus.OK);
    }

    @PutMapping(path = "/buyer/start/wifi-service/{purchase-id}/{status}")
    public ResponseEntity<?> startBuyerWifiService(
            @PathVariable(value = "purchase-id")
            @Range(min = 1, message = "{purchase.id.invalid}") Long purchaseId,
            @PathVariable(value = "status")
            @Range(min = 1, message = "{purchase.id.invalid}") int status) {
        Date wifiStartTime = purchaseService.startBuyerWifiService(purchaseId, status);
        return new ResponseEntity<>(wifiStartTime, HttpStatus.OK);
    }

    @PutMapping(path = "/buyer/update/wifi-service/{purchase-id}/{status}/{data-used}")
    public ResponseEntity<?> updateBuyerWifiService(
            @PathVariable(value = "purchase-id")
            @Range(min = 1, message = "{purchase.id.invalid}") Long purchaseId,
            @PathVariable(value = "status")
            @Range(min = 1, message = "{purchase.id.invalid}") int status,
            @PathVariable(value = "data-used")
            @DecimalMin(Constants.MINIMUM_DATA_USED_MB) double dataUsed) {
        int updateStatus = purchaseService.updateBuyerWifiService(purchaseId, status, dataUsed);
        return new ResponseEntity<>(updateStatus, HttpStatus.OK);
    }

    @PutMapping(path = "/buyer/finish/wifi-service/{purchase-id}/{status}/{data-used}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> finishBuyerWifiService(
            @PathVariable(value = "purchase-id")
            @Range(min = 1, message = "{purchase.id.invalid}") Long purchaseId,
            @PathVariable(value = "status")
            @Range(min = 1, message = "{purchase.id.invalid}") int status,
            @PathVariable(value = "data-used")
            @DecimalMin(Constants.MINIMUM_DATA_USED_MB) double dataUsed) {
        WifiSummaryResponse wifiSummaryResponse = purchaseService.finishBuyerWifiService(purchaseId, status, dataUsed);
        return new ResponseEntity<>(wifiSummaryResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/get/buyer/usages/date-time/{buyer-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedWifiUsagesDateTime(
            @PathVariable(value = "buyer-id")
            @Range(min = 1, message = "{buyer.id.invalid}") Long buyerId,
            @PathVariable(value = "page")
            @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
            @PathVariable(value = "size")
            @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
            @PathVariable(value = "is-descending") boolean isDescending) {
        List<WifiSummaryResponse> wifiSummaryResponses = purchaseService.getSortedWifiUsagesDateTime(buyerId, page, size, isDescending);
        return new ResponseEntity<>(wifiSummaryResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/get/buyer/usages/data-used/{buyer-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedWifiUsagesDataUsed(
            @PathVariable(value = "buyer-id")
            @Range(min = 1, message = "{buyer.id.invalid}") Long buyerId,
            @PathVariable(value = "page")
            @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
            @PathVariable(value = "size")
            @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
            @PathVariable(value = "is-descending") boolean isDescending) {
        List<WifiSummaryResponse> wifiSummaryResponses = purchaseService.getSortedWifiUsagesDataUsed(buyerId, page, size, isDescending);
        return new ResponseEntity<>(wifiSummaryResponses, HttpStatus.OK);
    }

    @PutMapping(path = "/withdraw/buyer/refunds/{buyer-id}")
    public ResponseEntity<?> withdrawBuyerRefunds(@PathVariable(value = "buyer-id")
                                                  @Range(min = 1, message = "{buyer.id.invalid}") Long buyerId) {
        RefundReceiptResponse refundReceiptResponse = purchaseService.withdrawBuyerRefunds(buyerId);
        return new ResponseEntity<>(refundReceiptResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/get/buyer/refunds/receipts/{buyer-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBuyerRefundReceipts(
            @PathVariable(value = "buyer-id")
            @Range(min = 1, message = "{buyer.id.invalid}") Long buyerId,
            @PathVariable(value = "page")
            @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
            @PathVariable(value = "size")
            @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
            @PathVariable(value = "is-descending") boolean isDescending) {
        List<RefundReceiptResponse> refundReceiptResponses = purchaseService.getBuyerRefundReceipts(buyerId, page, size, isDescending);
        return new ResponseEntity<>(refundReceiptResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/get/buyer/current-session-legit/{buyer-id}/{session-id}/{data-to-be-used}")
    public ResponseEntity<?> isBuyerCurrentSessionLegit(@PathVariable(value = "buyer-id")
                                                        @Range(min = 1, message = "{buyer.id.invalid}") Long buyerId,
                                                        @PathVariable(value = "session-id")
                                                        @Range(min = 1, message = "{session.id.invalid}") Long sessionId,
                                                        @PathVariable(value = "data-to-be-used")
                                                        @Range(min = Constants.MINIMUM_SELLING_DATA, max = Constants.MAXIMUM_SELLING_DATA, message = "{page.number.invalid}") int dataToBeUsed) {
        boolean isBuyerCurrentSessionLegit = purchaseService.isCurrentSessionLegit(buyerId, sessionId, dataToBeUsed);
        return new ResponseEntity<>(isBuyerCurrentSessionLegit, HttpStatus.OK);
    }

}
