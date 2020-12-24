package com.api.hotifi.payment.service;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.repository.UserRepository;
import com.api.hotifi.payment.entity.Purchase;
import com.api.hotifi.payment.repository.PurchaseRepository;
import com.api.hotifi.payment.web.request.PurchaseRequest;
import com.api.hotifi.payment.web.response.PurchaseReceiptResponse;
import com.api.hotifi.payment.web.response.WifiSummaryResponse;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.repository.SessionRepository;
import com.api.hotifi.speed_test.entity.SpeedTest;
import com.api.hotifi.speed_test.repository.SpeedTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PurchaseServiceImpl implements IPurchaseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpeedTestRepository speedTestRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Transactional
    @Override
    public PurchaseReceiptResponse addPurchase(PurchaseRequest purchaseRequest) {
        try {
            Long buyerId = purchaseRequest.getBuyerId();
            Long sessionId = purchaseRequest.getSessionId();
            User buyer = userRepository.getOne(buyerId);
            if (!LegitUtils.isBuyerLegit(buyer))
                throw new Exception("User not legit to be added");

            Session session = sessionRepository.findById(sessionId).orElse(null);
            if (session == null)
                throw new Exception("Session doesn't exist");
            if (session.getEndTime() != null)
                throw new Exception("Session already ended");

            Long sellerId = session.getSpeedTest().getUser().getId();
            if (sellerId.equals(buyerId))
                throw new Exception("Buyer and seller cannot be same");

            Purchase purchase = new Purchase();
            purchase.setPaymentId(purchaseRequest.getPaymentId());
            purchase.setSession(session);
            purchase.setStatus(purchaseRequest.getStatus());
            purchase.setUser(buyer);
            purchase.setMacAddress(purchaseRequest.getMacAddress());
            purchase.setData(purchaseRequest.getData());
            purchase.setAmountPaid(purchase.getAmountPaid());

            Long purchaseId = purchaseRepository.save(purchase).getId();

            return getPurchaseReceipt(purchaseId);

        } catch (Exception e) {
            log.error("Error occurred", e);
        }
        return null;
    }

    @Override
    @Transactional
    public PurchaseReceiptResponse getPurchaseReceipt(Long purchaseId) {
        try {
            Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
            if (purchase == null)
                throw new Exception("Purchase doesn't exist");
            String buyerUpiId = purchase.getUser().getUpiId();
            PurchaseReceiptResponse receiptResponse = new PurchaseReceiptResponse();
            receiptResponse.setPurchaseId(purchase.getId());
            receiptResponse.setPaymentId(purchase.getPaymentId());
            receiptResponse.setCreatedAt(purchase.getCreatedAt());
            receiptResponse.setPurchaseStatus(purchase.getStatus());
            receiptResponse.setAmountPaid(purchase.getAmountPaid());
            receiptResponse.setBuyerUpiId(buyerUpiId);
            receiptResponse.setHotifiUpiId(Constants.HOTIFI_UPI_ID);

            return receiptResponse;
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public Date startBuyerWifiService(Long purchaseId, int status) {
        try {
            Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
            if (purchase == null)
                throw new Exception("Purchase doesn't exist");
            if (purchase.getSessionStartedAt() != null)
                throw new Exception("Buyer's wifi service already started");
            if (purchase.getSessionFinishedAt() != null)
                throw new Exception("Buyer's wifi service finished. Cannot update start time");
            Date now = new Date(System.currentTimeMillis());
            purchase.setSessionStartedAt(now);
            purchase.setStatus(status);
            purchaseRepository.save(purchase);
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public int updateBuyerDataUsed(Long purchaseId, int status, double dataUsed) {
        try {
            Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
            if (LegitUtils.isPurchaseUpdateLegit(purchase)) {

                int dataBought = purchase.getData();
                if (Double.compare(dataUsed, dataBought) > 0)
                    throw new Exception("Data used " + dataUsed + " MB cannot be greater than data bought " + dataBought);

                int purchaseStatus = purchase.getStatus() == 0 ? status : purchase.getStatus();

                purchase.setDataUsed(dataUsed);
                purchase.setStatus(purchaseStatus);
                purchaseRepository.save(purchase);

                if (dataUsed > 0.9 * dataBought) {
                    log.info("90% data consumed");
                    return 1;
                }

                if (dataBought - dataUsed > Constants.MINIMUM_DATA_THRESHOLD_MB) {
                    //TODO logic to update purchase status
                    Date now = new Date(System.currentTimeMillis());
                    purchase.setStatus(purchaseStatus);
                    purchase.setAmountRefund(calculateRefundAmount(dataBought, dataUsed, purchase.getAmountPaid()));
                    purchase.setSessionFinishedAt(now);
                    purchaseRepository.save(purchase);
                    log.info("Finishing wifi service...");
                    return 2;
                }
            }

            return 0;

        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return -1;
    }

    @Override
    @Transactional
    public WifiSummaryResponse endBuyerWifiService(Long purchaseId, int status, double dataUsed) {
        try {
            Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
            if (LegitUtils.isPurchaseUpdateLegit(purchase)) {
                int dataBought = purchase.getData();
                if (Double.compare(dataUsed, dataBought) > 0)
                    throw new Exception("Data used " + dataUsed + " MB cannot be greater than data bought " + dataBought);

                int purchaseStatus = purchase.getStatus() == 0 ? status : purchase.getStatus();

                Date sessionFinishedAt = new Date(System.currentTimeMillis());
                double amountRefund = calculateRefundAmount(dataBought, dataUsed, purchase.getAmountPaid());

                //TODO logic to update purchase status
                purchase.setStatus(purchaseStatus);
                purchase.setAmountRefund(amountRefund);
                purchase.setSessionFinishedAt(sessionFinishedAt);
                purchaseRepository.save(purchase);
                log.info("Finishing wifi service...");

                return getBuyerWifiSummary(purchase);
            }

        } catch (Exception e) {
            log.error("Error Occurred ", e);
        }

        return null;
    }

    @Transactional
    @Override
    public List<WifiSummaryResponse> getSortedWifiUsagesDateTime(Long buyerId, int pageNumber, int elements, boolean isDescending) {
        try {
            Pageable pageable = isDescending ?
                    PageRequest.of(pageNumber, elements, Sort.by("session_started_at").descending())
                    : PageRequest.of(pageNumber, elements, Sort.by("session_started_at"));
            List<Purchase> purchases = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable);
            List<WifiSummaryResponse> wifiSummaryResponses = new ArrayList<>();

            for (Purchase purchase : purchases) {
                WifiSummaryResponse wifiSummaryResponse = getBuyerWifiSummary(purchase);
                wifiSummaryResponses.add(wifiSummaryResponse);
            }

            return wifiSummaryResponses;
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public List<WifiSummaryResponse> getSortedWifiUsagesDataUsed(Long buyerId, int pageNumber, int elements, boolean isDescending) {
        try {
            Pageable pageable = isDescending ?
                    PageRequest.of(pageNumber, elements, Sort.by("data_used").descending())
                    : PageRequest.of(pageNumber, elements, Sort.by("data_used"));
            List<Purchase> purchases = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable);
            List<WifiSummaryResponse> wifiSummaryResponses = new ArrayList<>();

            for (Purchase purchase : purchases) {
                WifiSummaryResponse wifiSummaryResponse = getBuyerWifiSummary(purchase);
                wifiSummaryResponses.add(wifiSummaryResponse);
            }

            return wifiSummaryResponses;
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return null;
    }

    public double calculateRefundAmount(double dataBought, double dataUsed, double amountPaid) {
        //Do not check for error types, simply calculate and return refund amount
        return (amountPaid / dataBought) * dataUsed;
    }

    public WifiSummaryResponse getBuyerWifiSummary(Purchase purchase) {
        Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
        assert session != null;
        SpeedTest speedTest = speedTestRepository.findById(session.getSpeedTest().getId()).orElse(null);
        assert speedTest != null;
        User seller = userRepository.findById(speedTest.getUser().getId()).orElse(null);
        assert seller != null;

        WifiSummaryResponse wifiSummaryResponse = new WifiSummaryResponse();

        wifiSummaryResponse.setSellerName(seller.getFirstName() + " " + seller.getLastName());
        wifiSummaryResponse.setSellerPhotoUrl(seller.getPhotoUrl());
        wifiSummaryResponse.setAmountPaid(purchase.getAmountPaid());
        wifiSummaryResponse.setAmountRefund(purchase.getAmountRefund());
        wifiSummaryResponse.setSessionStartedAt(purchase.getSessionStartedAt());
        wifiSummaryResponse.setSessionFinishedAt(purchase.getSessionFinishedAt());
        wifiSummaryResponse.setDataBought(purchase.getData());
        wifiSummaryResponse.setDataUsed(purchase.getDataUsed());

        return wifiSummaryResponse;
    }

    //TODO payment start and end methods of XYZ Payment Service

}