package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.utils.AESUtils;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.repositories.SellerPaymentRepository;
import com.api.hotifi.payment.services.interfaces.IPurchaseService;
import com.api.hotifi.payment.services.interfaces.ISellerPaymentService;
import com.api.hotifi.payment.utils.PaymentUtils;
import com.api.hotifi.payment.web.request.PurchaseRequest;
import com.api.hotifi.payment.web.responses.PurchaseReceiptResponse;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.WifiSummaryResponse;
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

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private SellerPaymentRepository sellerPaymentRepository;

    @Autowired
    private ISellerPaymentService sellerPaymentService;

    @Transactional(readOnly = true)
    @Override
    public boolean isCurrentSessionLegit(Long buyerId, Long sessionId, int dataToBeUsed){
        try{
        //check for invalid inputs and invalid razorpay payment id checks
            User buyer = userRepository.findById(buyerId).orElse(null);
            Session session = sessionRepository.findById(sessionId).orElse(null);

            if (!LegitUtils.isBuyerLegit(buyer))
                throw new Exception("Buyer not legit to be added");
            if (session == null)
                throw new Exception("Session doesn't exist");
            if (session.getFinishedAt() != null)
                throw new Exception("Session already ended");
            if (Double.compare(dataToBeUsed, (double) session.getData() - session.getDataUsed()) > 0)
                throw new Exception("Data to be bought " + dataToBeUsed + " MB > " + ((double) session.getData() - session.getDataUsed()) + " MB (available data)");

            Long sellerId = session.getSpeedTest().getUser().getId();
            if (sellerId.equals(buyerId))
                throw new Exception("Buyer and seller cannot be same");

            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

            //TODO configure status
            double totalPendingRefunds = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable)
                    .stream()
                    .filter(purchase -> purchase.getStatus() == 4)
                    .mapToDouble(Purchase::getAmountRefund)
                    .sum();

            if(totalPendingRefunds > Constants.MAXIMUM_REFUND_WITHDRAWAL_LIMIT)
                throw new Exception("Withdraw pending refunds first to start a purchase");

            return true;

        }catch (Exception e){
            log.error("Error occurred ", e);
        }
        return false;
    }

    @Transactional
    @Override
    public PurchaseReceiptResponse addPurchase(PurchaseRequest purchaseRequest) {
        try {
            //TODO check razorpay valid payment id if not valid throw exception
            // purchaseRequest.getPaymentId() check for validation
            Long buyerId = purchaseRequest.getBuyerId();
            Long sessionId = purchaseRequest.getSessionId();
            Session session = sessionRepository.findById(sessionId).orElse(null);
            User buyer = userRepository.findById(buyerId).orElse(null);

            assert session != null;
            if (session.getFinishedAt() != null) {
                //instead of throwing exception update the purchase status
                //TODO inititate razorpay refund
            }
            if (Double.compare(purchaseRequest.getData(), (double) session.getData() - session.getDataUsed()) > 0) {
                //instead of throwing exception update the purchase status
                //TODO inititate razorpay refund
            }

            Purchase purchase = new Purchase();
            purchase.setSession(session);
            purchase.setUser(buyer);
            purchase.setPaymentDoneAt(purchaseRequest.getPaymentDoneAt());
            purchase.setPaymentId(purchaseRequest.getPaymentId());
            purchase.setStatus(purchaseRequest.getStatus());
            purchase.setMacAddress(purchaseRequest.getMacAddress());
            purchase.setData(purchaseRequest.getData());
            purchase.setAmountPaid(purchaseRequest.getAmountPaid());
            purchase.setAmountRefund(purchaseRequest.getAmountPaid());

            Long purchaseId = purchaseRepository.save(purchase).getId();

            //saving session data used column
            //TODO add a condition to check if the payment status is successfully done, then update session
            session.setDataUsed(session.getDataUsed() + purchaseRequest.getData());
            sessionRepository.save(session);

            return getPurchaseReceipt(purchaseId);

        } catch (Exception e) {
            log.error("Error occurred", e);
        }
        return null;
    }

    @Transactional
    @Override
    public PurchaseReceiptResponse getPurchaseReceipt(Long purchaseId) {
        try {
            Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);

            if (purchase == null)
                throw new Exception("Purchase doesn't exist");

            String buyerUpiId = purchase.getUser().getUpiId();
            Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
            String wifiPassword = session != null ? session.getWifiPassword() : null;
            PurchaseReceiptResponse receiptResponse = new PurchaseReceiptResponse();
            receiptResponse.setPurchaseId(purchase.getId());
            receiptResponse.setPaymentId(purchase.getPaymentId());
            receiptResponse.setCreatedAt(purchase.getPaymentDoneAt());
            receiptResponse.setPurchaseStatus(purchase.getStatus());
            receiptResponse.setAmountPaid(purchase.getAmountPaid());
            receiptResponse.setBuyerUpiId(buyerUpiId);
            receiptResponse.setHotifiUpiId(Constants.HOTIFI_UPI_ID);
            receiptResponse.setWifiPassword(AESUtils.decrypt(wifiPassword, Constants.WIFI_PASSWORD_SECRET_KEY));

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
            if (purchase.getSessionCreatedAt() != null)
                throw new Exception("Buyer's wifi service already started");
            if (purchase.getSessionFinishedAt() != null)
                throw new Exception("Buyer's wifi service finished. Cannot update start time");
            Date sessionStartedAt = new Date(System.currentTimeMillis());
            purchase.setSessionCreatedAt(sessionStartedAt);
            purchase.setStatus(status);
            purchaseRepository.save(purchase);
            return sessionStartedAt;
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public int updateBuyerWifiService(Long purchaseId, int status, double dataUsed) {
        try {
            Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);

            if (LegitUtils.isPurchaseUpdateLegit(purchase, dataUsed)) {
                int dataBought = purchase.getData();
                int purchaseStatus = purchase.getStatus() == 0 ? status : purchase.getStatus();
                Date now = new Date(System.currentTimeMillis());
                purchase.setDataUsed(dataUsed);
                purchase.setAmountRefund(calculateRefundAmount(dataBought, dataUsed, purchase.getAmountPaid()));

                //Updating seller payment each time update is made
                Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
                User seller = session != null ? session.getSpeedTest().getUser() : null;
                SellerPayment sellerPayment = seller != null ? sellerPaymentRepository.findSellerPaymentBySellerId(seller.getId()) : null;
                if (sellerPayment == null)
                    sellerPaymentService.addSellerPayment(seller, purchase.getAmountPaid() - purchase.getAmountRefund());
                else
                    sellerPaymentService.updateSellerPayment(seller, purchase.getAmountPaid() - purchase.getAmountRefund());

                //Comparing if data is going to be exhausted
                if (Double.compare((double) dataBought - dataUsed, Constants.MINIMUM_DATA_THRESHOLD_MB) < 0) {
                    saveFinishWifiService(purchase, purchaseStatus, now);
                    return 2;
                }

                if (Double.compare(dataUsed, 0.9 * dataBought) > 0) {
                    log.info("90% data consumed");
                    return 1;
                }

                purchase.setSessionModifiedAt(now);
                purchase.setStatus(purchaseStatus);
                purchaseRepository.save(purchase);

            }

            return 0;

        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return -1;
    }

    @Override
    @Transactional
    public WifiSummaryResponse finishBuyerWifiService(Long purchaseId, int status, double dataUsed) {
        try {
            Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
            if (LegitUtils.isPurchaseUpdateLegit(purchase, dataUsed)) {
                int dataBought = purchase.getData();
                if (Double.compare(dataUsed, dataBought) > 0)
                    throw new Exception("Data used " + dataUsed + " MB cannot be greater than data bought " + dataBought);
                if (Double.compare(dataUsed, purchase.getDataUsed()) < 0)
                    throw new Exception("New Data used " + dataUsed + " MB cannot be less than data used " + purchase.getDataUsed());

                int purchaseStatus = purchase.getStatus() == 0 ? status : purchase.getStatus();
                Date sessionFinishedAt = new Date(System.currentTimeMillis());
                double amountRefund = calculateRefundAmount(dataBought, dataUsed, purchase.getAmountPaid());

                purchase.setDataUsed(dataUsed);
                purchase.setAmountRefund(amountRefund);
                saveFinishWifiService(purchase, purchaseStatus, sessionFinishedAt);

                return getBuyerWifiSummary(purchase);
            }

        } catch (Exception e) {
            log.error("Error Occurred ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public List<WifiSummaryResponse> getSortedWifiUsagesDateTime(Long buyerId, int page, int size, boolean isDescending) {
        try {
            Pageable pageable = isDescending ?
                    PageRequest.of(page, size, Sort.by("session_started_at").descending())
                    : PageRequest.of(page, size, Sort.by("session_started_at"));
            return getWifiSummaryResponses(buyerId, pageable);
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public List<WifiSummaryResponse> getSortedWifiUsagesDataUsed(Long buyerId, int page, int size, boolean isDescending) {
        try {
            Pageable pageable = isDescending ?
                    PageRequest.of(page, size, Sort.by("data_used").descending())
                    : PageRequest.of(page, size, Sort.by("data_used"));
            return getWifiSummaryResponses(buyerId, pageable);
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public RefundReceiptResponse withdrawBuyerRefunds(Long buyerId) {
        try {
            User buyer = userRepository.findById(buyerId).orElse(null);
            if (LegitUtils.isBuyerLegit(buyer)) {
                Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
                double totalRefundAmount = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable)
                        .stream().filter(purchase -> purchase.getStatus() == 4)
                        .mapToDouble(Purchase::getAmountRefund)
                        .sum();//TODO Status check


                //TODO Razorpay implementation for refunds
                String refundPaymentId = "1234";
                Date refundDoneAt = new Date(System.currentTimeMillis());
                List<Long> purchaseIds = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable)
                        .stream().filter(purchase -> purchase.getStatus() == 4)
                        .map(Purchase::getId)
                        .collect(Collectors.toList());
                purchaseRepository.updatePurchaseRefundStatus(purchaseIds,5, refundPaymentId, refundDoneAt);

                return getBuyerRefundReceipts(buyerId, 0, Integer.MAX_VALUE, true).get(0); //It is always going to return 1 record
            }
        } catch (Exception e) {
            log.error("Error Occurred ", e);
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RefundReceiptResponse> getBuyerRefundReceipts(Long buyerId, int page, int size, boolean isDescending) {
        try {
            User buyer = userRepository.findById(buyerId).orElse(null);
            if (LegitUtils.isBuyerLegit(buyer)) {
                Pageable pageable = isDescending ? PageRequest.of(page, size, Sort.by("refund_done_at").descending()) :
                        PageRequest.of(page, size, Sort.by("refund_done_at"));
                Map<String, List<Purchase>> purchaseReceipts = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable)
                        .stream()
                        .collect(Collectors.groupingBy(Purchase::getRefundPaymentId));

                List<RefundReceiptResponse> refundReceiptResponses = new ArrayList<>();

                purchaseReceipts.forEach((refundPaymentId, purchases) -> {
                    //TODO update for processing payments
                    purchases.forEach(purchase -> {

                        if (purchase.getStatus() == 4) {
                            //TODO Razorpay check for payment status, initiate payment again if failed
                            int status = purchase.getStatus() + 1;
                            List<Long> purchaseIds = Collections.singletonList(purchase.getId());
                            Date refundDoneAt = new Date(System.currentTimeMillis());

                            purchaseRepository.updatePurchaseRefundStatus(purchaseIds, status, refundPaymentId, refundDoneAt);
                            purchase.setStatus(status);
                            purchase.setRefundPaymentId(refundPaymentId);
                            purchase.setRefundDoneAt(refundDoneAt);

                        }

                        RefundReceiptResponse refundReceiptResponse = new RefundReceiptResponse(purchase, refundPaymentId, purchase.getRefundDoneAt(), Constants.HOTIFI_UPI_ID, purchase.getUser().getUpiId());
                        refundReceiptResponses.add(refundReceiptResponse);

                    });

                });

                return refundReceiptResponses;

            }
        } catch (Exception e) {
            log.error("Error Occurred ", e);
        }
        return null;
    }

    //User defined functions
    private double calculateRefundAmount(double dataBought, double dataUsed, double amountPaid) {
        //Do not check for error types, simply calculate and return refund amount
        return amountPaid - Math.floor((amountPaid / dataBought) * dataUsed);
    }

    private WifiSummaryResponse getBuyerWifiSummary(Purchase purchase) {
        Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
        assert session != null;
        SpeedTest speedTest = speedTestRepository.findById(session.getSpeedTest().getId()).orElse(null);
        assert speedTest != null;
        User seller = userRepository.findById(speedTest.getUser().getId()).orElse(null);
        assert seller != null;

        RefundReceiptResponse refundReceiptResponse = withdrawBuyerRefunds(purchase.getUser().getId());

        WifiSummaryResponse wifiSummaryResponse = new WifiSummaryResponse();

        wifiSummaryResponse.setSellerName(seller.getFirstName() + " " + seller.getLastName());
        wifiSummaryResponse.setSellerPhotoUrl(seller.getPhotoUrl());
        wifiSummaryResponse.setAmountPaid(purchase.getAmountPaid());
        wifiSummaryResponse.setAmountRefund(purchase.getAmountRefund());
        wifiSummaryResponse.setSessionStartedAt(purchase.getSessionCreatedAt());
        wifiSummaryResponse.setSessionFinishedAt(purchase.getSessionFinishedAt());
        wifiSummaryResponse.setDataBought(purchase.getData());
        wifiSummaryResponse.setDataUsed(purchase.getDataUsed());
        wifiSummaryResponse.setRefundReceiptResponse(refundReceiptResponse);

        return wifiSummaryResponse;
    }

    private void saveFinishWifiService(Purchase purchase, int purchaseStatus, Date sessionFinishedAt) {
        //TODO logic to update purchase status
        purchase.setStatus(purchaseStatus);
        purchase.setSessionFinishedAt(sessionFinishedAt);
        purchaseRepository.save(purchase);

        Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
        assert session != null;
        session.setDataUsed(PaymentUtils.getDataUsedSumOfSession(session));
        session.setModifiedAt(sessionFinishedAt);
        sessionRepository.save(session);

    }

    private List<WifiSummaryResponse> getWifiSummaryResponses(Long buyerId, Pageable pageable) {
        List<Purchase> purchases = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable);
        List<WifiSummaryResponse> wifiSummaryResponses = new ArrayList<>();

        for (Purchase purchase : purchases) {
            WifiSummaryResponse wifiSummaryResponse = getBuyerWifiSummary(purchase);
            wifiSummaryResponses.add(wifiSummaryResponse);
        }

        return wifiSummaryResponses;
    }

    //TODO payment start and end methods of RazorPay Payment Service

}