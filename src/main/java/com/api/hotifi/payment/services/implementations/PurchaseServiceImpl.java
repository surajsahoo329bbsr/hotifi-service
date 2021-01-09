package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.utils.AESUtils;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.error.PurchaseErrorCodes;
import com.api.hotifi.payment.processor.PaymentProcessor;
import com.api.hotifi.payment.processor.codes.BuyerPaymentCodes;
import com.api.hotifi.payment.processor.codes.PaymentGatewayCodes;
import com.api.hotifi.payment.processor.codes.PaymentMethodCodes;
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
import java.util.stream.Stream;

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
    public boolean isCurrentSessionLegit(Long buyerId, Long sessionId, int dataToBeUsed) {
        //check for invalid inputs and invalid razorpay payment id checks
        User buyer = userRepository.findById(buyerId).orElse(null);
        Session session = sessionRepository.findById(sessionId).orElse(null);

        if (!LegitUtils.isBuyerLegit(buyer))
            throw new HotifiException(PurchaseErrorCodes.BUYER_NOT_LEGIT);
        if (session == null)
            throw new HotifiException(PurchaseErrorCodes.NO_SESSION_EXISTS);
        if (session.getFinishedAt() != null)
            throw new HotifiException(PurchaseErrorCodes.SESSION_ALREADY_ENDED);
        if (Double.compare(dataToBeUsed, (double) session.getData() - session.getDataUsed()) > 0)
            throw new HotifiException(PurchaseErrorCodes.EXCESS_DATA_TO_BUY_ERROR);

        Long sellerId = session.getSpeedTest().getUser().getId();
        if (sellerId.equals(buyerId))
            throw new HotifiException(PurchaseErrorCodes.BUYER_SELLER_SAME);

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        double totalPendingRefunds = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable)
                .stream()
                .filter(purchase -> purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE >= BuyerPaymentCodes.PAYMENT_SUCCESSFUL.value() &&
                        purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE <= BuyerPaymentCodes.PAYMENT_FAILED.value())
                .mapToDouble(Purchase::getAmountRefund)
                .sum();

        if (totalPendingRefunds > Constants.MAXIMUM_REFUND_WITHDRAWAL_LIMIT)
            throw new HotifiException(PurchaseErrorCodes.WITHDRAW_PENDING_REFUNDS);

        return true;
    }

    @Transactional
    @Override
    public PurchaseReceiptResponse addPurchase(PurchaseRequest purchaseRequest) {

        try {
            Long buyerId = purchaseRequest.getBuyerId();
            Long sessionId = purchaseRequest.getSessionId();
            Session session = sessionRepository.findById(sessionId).orElse(null);
            User buyer = userRepository.findById(buyerId).orElse(null);
            PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentMethodCodes.UPI_PAYMENT_METHOD, PaymentGatewayCodes.RAZORPAY);

            BuyerPaymentCodes buyerPaymentCodes = BuyerPaymentCodes.fromInt(paymentProcessor.getBuyerPaymentStatus(purchaseRequest.getPaymentId()).getPurchase().getStatus());
            Purchase purchase = new Purchase();
            purchase.setSession(session);
            purchase.setUser(buyer);
            purchase.setPaymentDoneAt(purchaseRequest.getPaymentDoneAt());
            purchase.setPaymentId(purchaseRequest.getPaymentId());
            purchase.setStatus(buyerPaymentCodes.value() * paymentProcessor.getPaymentMethodCodes().getUpiPaymentMethod().value());
            purchase.setMacAddress(purchaseRequest.getMacAddress());
            purchase.setData(purchaseRequest.getData());
            purchase.setAmountPaid(purchaseRequest.getAmountPaid());
            purchase.setAmountRefund(purchaseRequest.getAmountPaid());

            boolean updateSessionFlag = true;

            //If payment failed or processing do not update session value
            if (buyerPaymentCodes.value() <= BuyerPaymentCodes.PAYMENT_PROCESSING.value())
                updateSessionFlag = false;
                //If seller has ended it's session while purchasing, inititate refund if payment is successful
            else if (session != null && session.getFinishedAt() != null && buyer != null) {
                if (buyerPaymentCodes.value() == BuyerPaymentCodes.PAYMENT_SUCCESSFUL.value() % paymentProcessor.getPaymentMethodCodes().value()) {
                    RefundReceiptResponse receiptResponse = paymentProcessor.startBuyerRefund(purchaseRequest.getAmountPaid(), buyer.getUpiId(), buyer.getAuthentication().getEmail());
                    purchase.setStatus(receiptResponse.getPurchase().getStatus());
                    purchase.setRefundDoneAt(receiptResponse.getRefundDoneAt());
                    purchase.setRefundPaymentId(receiptResponse.getRefundPaymentId());
                    updateSessionFlag = false;
                    log.info("Razor Refund Payment");
                }
            } else if (session != null && buyer != null && Double.compare(purchaseRequest.getData(), (double) session.getData() - session.getDataUsed()) > 0) {
                if (buyerPaymentCodes.value() == BuyerPaymentCodes.PAYMENT_SUCCESSFUL.value() % paymentProcessor.getPaymentMethodCodes().value()) {
                    RefundReceiptResponse receiptResponse = paymentProcessor.startBuyerRefund(purchaseRequest.getAmountPaid(), buyer.getUpiId(), buyer.getAuthentication().getEmail());
                    purchase.setStatus(receiptResponse.getPurchase().getStatus());
                    purchase.setRefundDoneAt(receiptResponse.getRefundDoneAt());
                    purchase.setRefundPaymentId(receiptResponse.getRefundPaymentId());
                    updateSessionFlag = false;
                    log.info("Razor Refund Payment");
                }
            }

            Long purchaseId = purchaseRepository.save(purchase).getId();

            //saving session data used column
            if (session != null && updateSessionFlag) {
                session.setDataUsed(session.getDataUsed() + purchaseRequest.getData());
                sessionRepository.save(session);
            }

            return getPurchaseReceipt(purchaseId);

        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new HotifiException(PurchaseErrorCodes.UNEXPECTED_PURCHASE_ERROR);
        }
    }

    @Transactional
    @Override
    public PurchaseReceiptResponse getPurchaseReceipt(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
        if (purchase == null)
            throw new HotifiException(PurchaseErrorCodes.NO_PURCHASE_EXISTS);
        try {
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
            if (purchase.getRefundPaymentId() != null) {
                receiptResponse.setRefundDoneAt(purchase.getRefundDoneAt());
                receiptResponse.setRefundPaymentId(purchase.getRefundPaymentId());
                receiptResponse.setWifiPassword(null);
            }
            return receiptResponse;
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(PurchaseErrorCodes.UNEXPECTED_PURCHASE_ERROR);
        }
    }

    @Transactional
    @Override
    public Date startBuyerWifiService(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
        if (purchase == null)
            throw new HotifiException(PurchaseErrorCodes.NO_PURCHASE_EXISTS);
        if (purchase.getSessionCreatedAt() != null)
            throw new HotifiException(PurchaseErrorCodes.BUYER_WIFI_SERVICE_ALREADY_STARTED);
        if (purchase.getSessionFinishedAt() != null)
            throw new HotifiException(PurchaseErrorCodes.BUYER_WIFI_SERVICE_ALREADY_FINISHED);
        if (purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE <= BuyerPaymentCodes.PAYMENT_PROCESSING.value())
            throw new HotifiException(PurchaseErrorCodes.UNEXPECTED_PURCHASE_ERROR);
        try {
            Date sessionStartedAt = new Date(System.currentTimeMillis());
            purchase.setSessionCreatedAt(sessionStartedAt);
            purchase.setStatus(BuyerPaymentCodes.UPDATE_WIFI_SERVICE.value() + (purchase.getStatus() - (purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE)));
            purchaseRepository.save(purchase);
            return sessionStartedAt;
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(PurchaseErrorCodes.UNEXPECTED_PURCHASE_ERROR);
        }
    }

    @Transactional
    @Override
    public int updateBuyerWifiService(Long purchaseId, double dataUsed) {
        try {
            Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);

            if (LegitUtils.isPurchaseUpdateLegit(purchase, dataUsed)) {
                int dataBought = purchase.getData();
                int purchaseStatus = BuyerPaymentCodes.UPDATE_WIFI_SERVICE.value() + (purchase.getStatus() - (purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE));
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
                    log.info("Finish wifi service");
                    return 2;
                }

                if (Double.compare(dataUsed, 0.9 * dataBought) > 0) {
                    log.info("90% data consumed");
                    return 1;
                }

                purchase.setSessionModifiedAt(now);
                purchase.setStatus(purchaseStatus);
                purchaseRepository.save(purchase);
                return 0;
            }

            return -1;

        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(PurchaseErrorCodes.UNEXPECTED_PURCHASE_ERROR);
        }
    }

    @Override
    @Transactional
    public WifiSummaryResponse finishBuyerWifiService(Long purchaseId, double dataUsed) {

        Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
        PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentMethodCodes.UPI_PAYMENT_METHOD, PaymentGatewayCodes.RAZORPAY);

        if (LegitUtils.isPurchaseUpdateLegit(purchase, dataUsed)) {
            int dataBought = purchase.getData();
            Date sessionFinishedAt = new Date(System.currentTimeMillis());
            double amountRefund = calculateRefundAmount(dataBought, dataUsed, purchase.getAmountPaid());
            RefundReceiptResponse refundReceiptResponse = paymentProcessor.startBuyerRefund(amountRefund, purchase.getUser().getUpiId(), purchase.getUser().getAuthentication().getEmail());
            purchase.setStatus(refundReceiptResponse.getPurchase().getStatus() + (purchase.getStatus() - (purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE)));
            purchase.setDataUsed(dataUsed);
            purchase.setAmountRefund(amountRefund);
            saveFinishWifiService(purchase, purchase.getStatus(), sessionFinishedAt);
            return getBuyerWifiSummary(purchase, true);
        } else
            throw new HotifiException(PurchaseErrorCodes.PURCHASE_UPDATE_NOT_LEGIT);

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
            throw new HotifiException(PurchaseErrorCodes.UNEXPECTED_PURCHASE_ERROR);
        }
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
            throw new HotifiException(PurchaseErrorCodes.UNEXPECTED_PURCHASE_ERROR);
        }
    }

    @Transactional
    @Override
    public RefundReceiptResponse withdrawBuyerRefunds(Long buyerId) {
        User buyer = userRepository.findById(buyerId).orElse(null);
        if (LegitUtils.isBuyerLegit(buyer)) {
            PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentMethodCodes.UPI_PAYMENT_METHOD, PaymentGatewayCodes.RAZORPAY);
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Stream<Purchase> purchaseStream = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable).stream();
            double totalRefundAmount = purchaseStream.filter(purchase -> purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE >= BuyerPaymentCodes.PAYMENT_SUCCESSFUL.value() && purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE <= BuyerPaymentCodes.REFUND_FAILED.value())
                    .mapToDouble(Purchase::getAmountRefund)
                    .sum();

            List<Long> purchaseIds = purchaseStream.filter(purchase -> purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE >= BuyerPaymentCodes.PAYMENT_SUCCESSFUL.value() && purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE <= BuyerPaymentCodes.REFUND_FAILED.value())
                    .map(Purchase::getId)
                    .collect(Collectors.toList());

            RefundReceiptResponse buyerRefund = paymentProcessor.startBuyerRefund(totalRefundAmount, buyer.getUpiId(), buyer.getAuthentication().getEmail());

            String refundPaymentId = buyerRefund.getRefundPaymentId();
            Date refundDoneAt = buyerRefund.getRefundDoneAt();

            purchaseRepository.updatePurchaseRefundStatus(purchaseIds, BuyerPaymentCodes.PAYMENT_SUCCESSFUL.value(), refundPaymentId, refundDoneAt);

            return getBuyerRefundReceipts(buyerId, 0, Integer.MAX_VALUE, true).get(0); //It is always going to return 1 record
        } else
            throw new HotifiException(PurchaseErrorCodes.PURCHASE_UPDATE_NOT_LEGIT);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RefundReceiptResponse> getBuyerRefundReceipts(Long buyerId, int page, int size, boolean isDescending) {
        User buyer = userRepository.findById(buyerId).orElse(null);
        PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentMethodCodes.UPI_PAYMENT_METHOD, PaymentGatewayCodes.RAZORPAY);
        if (LegitUtils.isBuyerLegit(buyer)) {
            Pageable pageable = isDescending ? PageRequest.of(page, size, Sort.by("refund_done_at").descending()) :
                    PageRequest.of(page, size, Sort.by("refund_done_at"));
            Map<String, List<Purchase>> purchaseReceipts = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable)
                    .stream()
                    .collect(Collectors.groupingBy(Purchase::getRefundPaymentId));
            List<RefundReceiptResponse> refundReceiptResponses = new ArrayList<>();
            purchaseReceipts.forEach((refundPaymentId, purchases) -> purchases.forEach(purchase -> {
                if (purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE == BuyerPaymentCodes.PAYMENT_PROCESSING.value()) {
                    RefundReceiptResponse receiptResponse = paymentProcessor.getBuyerPaymentStatus(refundPaymentId);
                    int status = receiptResponse.getPurchase().getStatus();
                    List<Long> purchaseIds = Collections.singletonList(purchase.getId());
                    Date refundDoneAt = receiptResponse.getRefundDoneAt();

                    purchaseRepository.updatePurchaseRefundStatus(purchaseIds, status, refundPaymentId, refundDoneAt);
                    purchase.setStatus(status);
                    purchase.setRefundPaymentId(refundPaymentId);
                    purchase.setRefundDoneAt(refundDoneAt);
                }
                RefundReceiptResponse refundReceiptResponse = new RefundReceiptResponse(purchase, refundPaymentId, purchase.getRefundDoneAt(), Constants.HOTIFI_UPI_ID, purchase.getUser().getUpiId());
                refundReceiptResponses.add(refundReceiptResponse);
            }));
            return refundReceiptResponses;
        } else
            throw new HotifiException(PurchaseErrorCodes.BUYER_NOT_LEGIT);
    }

    //User defined functions

    private double calculateRefundAmount(double dataBought, double dataUsed, double amountPaid) {
        //Do not check for error types, simply calculate and return refund amount
        return amountPaid - Math.floor((amountPaid / dataBought) * dataUsed);
    }

    private WifiSummaryResponse getBuyerWifiSummary(Purchase purchase, boolean isWithdrawAmount) {
        Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
        SpeedTest speedTest = session != null ? speedTestRepository.findById(session.getSpeedTest().getId()).orElse(null) : null;
        User seller = speedTest != null ? userRepository.findById(speedTest.getUser().getId()).orElse(null) : null;

        if (seller == null)
            throw new HotifiException(UserErrorCodes.NO_USER_EXISTS);

        PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentMethodCodes.UPI_PAYMENT_METHOD, PaymentGatewayCodes.RAZORPAY);

        RefundReceiptResponse refundReceiptResponse = isWithdrawAmount ? withdrawBuyerRefunds(purchase.getUser().getId()) : paymentProcessor.getBuyerPaymentStatus(purchase.getPaymentId());
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
        purchase.setStatus(purchaseStatus);
        purchase.setSessionFinishedAt(sessionFinishedAt);
        purchaseRepository.save(purchase);
        Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
        if (session == null)
            throw new HotifiException(PurchaseErrorCodes.NO_SESSION_EXISTS);
        session.setDataUsed(PaymentUtils.getDataUsedSumOfSession(session));
        session.setModifiedAt(sessionFinishedAt);
        sessionRepository.save(session);
    }

    private List<WifiSummaryResponse> getWifiSummaryResponses(Long buyerId, Pageable pageable) {
        List<Purchase> purchases = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable);
        List<WifiSummaryResponse> wifiSummaryResponses = new ArrayList<>();
        for (Purchase purchase : purchases) {
            WifiSummaryResponse wifiSummaryResponse = getBuyerWifiSummary(purchase, false);
            wifiSummaryResponses.add(wifiSummaryResponse);
        }
        return wifiSummaryResponses;
    }

}