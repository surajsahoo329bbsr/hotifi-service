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
import com.api.hotifi.speedtest.entity.SpeedTest;
import com.api.hotifi.speedtest.repository.SpeedTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PurchaseServiceImpl implements IPurchaseService {

    private final UserRepository userRepository;
    private final SpeedTestRepository speedTestRepository;
    private final SessionRepository sessionRepository;
    private final PurchaseRepository purchaseRepository;
    private final SellerPaymentRepository sellerPaymentRepository;
    private final ISellerPaymentService sellerPaymentService;

    public PurchaseServiceImpl(UserRepository userRepository, SpeedTestRepository speedTestRepository, SessionRepository sessionRepository, PurchaseRepository purchaseRepository, SellerPaymentRepository sellerPaymentRepository, ISellerPaymentService sellerPaymentService) {
        this.userRepository = userRepository;
        this.speedTestRepository = speedTestRepository;
        this.sessionRepository = sessionRepository;
        this.purchaseRepository = purchaseRepository;
        this.sellerPaymentRepository = sellerPaymentRepository;
        this.sellerPaymentService = sellerPaymentService;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isCurrentSessionLegit(Long buyerId, Long sessionId, int dataToBeUsed) {
        //check for invalid inputs and invalid razorpay payment id checks
        User buyer = userRepository.findById(buyerId).orElse(null);
        Session session = sessionRepository.findById(sessionId).orElse(null);

        if (!LegitUtils.isBuyerLegit(buyer))
            throw new HotifiException(PurchaseErrorCodes.BUYER_NOT_LEGIT);
        if (session == null)
            throw new HotifiException(PurchaseErrorCodes.SESSION_NOT_FOUND);
        if (session.getFinishedAt() != null)
            throw new HotifiException(PurchaseErrorCodes.SESSION_ALREADY_ENDED);
        if (Double.compare(dataToBeUsed, (double) session.getData() - session.getDataUsed()) > 0)
            throw new HotifiException(PurchaseErrorCodes.EXCESS_DATA_TO_BUY_ERROR);

        Long sellerId = session.getSpeedTest().getUser().getId();
        if (sellerId.equals(buyerId))
            throw new HotifiException(PurchaseErrorCodes.BUYER_SELLER_SAME);

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        BigDecimal totalPendingRefunds = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable)
                .stream()
                .filter(purchase -> purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.PAYMENT_CAPTURED.value() &&
                        purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE < BuyerPaymentCodes.REFUND_PENDING.value())
                .map(Purchase::getAmountRefund)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPendingRefunds.compareTo(BigDecimal.valueOf(Constants.MAXIMUM_REFUND_WITHDRAWAL_LIMIT)) > 0)
            throw new HotifiException(PurchaseErrorCodes.WITHDRAW_PENDING_REFUNDS);

        return true;
    }

    @Transactional
    @Override
    public PurchaseReceiptResponse addPurchase(PurchaseRequest purchaseRequest) {

        Long buyerId = purchaseRequest.getBuyerId();
        Long sessionId = purchaseRequest.getSessionId();
        Session session = sessionRepository.findById(sessionId).orElse(null);
        User buyer = userRepository.findById(buyerId).orElse(null);
        PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY, PaymentMethodCodes.valueOf(purchaseRequest.getPaymentMethod()));

        try {
            BigDecimal amountPaid = session != null ?
                    session.getPrice()
                            .divide(BigDecimal.valueOf(Constants.UNIT_GB_VALUE_IN_MB), 2, RoundingMode.CEILING)
                            .multiply(BigDecimal.valueOf(purchaseRequest.getData()))
                            .setScale(0, RoundingMode.CEILING) : BigDecimal.ZERO;
            Purchase buyerPurchase = paymentProcessor.getBuyerPurchase(purchaseRequest.getPaymentId(), amountPaid);
            Purchase purchase = new Purchase();
            purchase.setSession(session);
            purchase.setUser(buyer);

            //Getting values from payment processor
            purchase.setPaymentDoneAt(buyerPurchase.getPaymentDoneAt());
            purchase.setPaymentId(buyerPurchase.getPaymentId());
            purchase.setPaymentTransactionId(buyerPurchase.getPaymentTransactionId());
            purchase.setStatus(buyerPurchase.getStatus());
            //Getting values from purchase request
            purchase.setMacAddress(purchaseRequest.getMacAddress());
            purchase.setIpAddress(purchase.getIpAddress());
            purchase.setData(purchaseRequest.getData());
            purchase.setAmountPaid(amountPaid);
            purchase.setAmountRefund(amountPaid);

            //If payment failed or processing do not update session value
            boolean isSessionCompleted = session != null && session.getFinishedAt() != null && buyer != null;
            boolean isBothMacAndIpAddressAbsent = purchaseRequest.getMacAddress() == null && purchaseRequest.getIpAddress() == null && session != null && buyer != null;
            boolean isDataBoughtMoreThanDataSold = session != null && buyer != null && Double.compare(purchaseRequest.getData(), (double) session.getData() - session.getDataUsed()) > 0;
            boolean isPurchaseAlreadySuccessful = buyerPurchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.PAYMENT_CAPTURED.value();

            if ((isSessionCompleted || isDataBoughtMoreThanDataSold) && isPurchaseAlreadySuccessful) {
                RefundReceiptResponse receiptResponse = paymentProcessor.startBuyerRefund(purchaseRepository, buyerPurchase.getAmountPaid(), purchaseRequest.getPaymentId());
                purchase.setStatus(receiptResponse.getPurchase().getStatus());
                purchase.setRefundDoneAt(receiptResponse.getPurchase().getRefundDoneAt());
                purchase.setRefundPaymentId(receiptResponse.getPurchase().getRefundPaymentId());
                purchase.setRefundTransactionId(receiptResponse.getPurchase().getRefundTransactionId());
                log.info("Razor Refund Payment");
            }

            Long purchaseId = purchaseRepository.save(purchase).getId();
            boolean updateSessionFlag = (!isSessionCompleted && !isBothMacAndIpAddressAbsent && !isDataBoughtMoreThanDataSold) || !isPurchaseAlreadySuccessful;

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
            throw new HotifiException(PurchaseErrorCodes.PURCHASE_NOT_FOUND);
        try {
            Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
            String wifiPassword = session != null ? session.getWifiPassword() : null;
            PurchaseReceiptResponse receiptResponse = new PurchaseReceiptResponse();
            receiptResponse.setPurchaseId(purchase.getId());
            receiptResponse.setPaymentId(purchase.getPaymentId());
            receiptResponse.setCreatedAt(purchase.getPaymentDoneAt());
            receiptResponse.setPurchaseStatus(purchase.getStatus());
            receiptResponse.setAmountPaid(purchase.getAmountPaid());
            receiptResponse.setHotifiBankAccount(Constants.HOTIFI_BANK_ACCOUNT);
            receiptResponse.setWifiPassword(AESUtils.decrypt(wifiPassword, Constants.AES_PASSWORD_SECRET_KEY));
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
            throw new HotifiException(PurchaseErrorCodes.PURCHASE_NOT_FOUND);
        if (purchase.getRefundStartedAt() != null)
            throw new HotifiException(PurchaseErrorCodes.BUYER_WIFI_SERVICE_ALREADY_FINISHED);
        if (purchase.getSessionCreatedAt() != null)
            throw new HotifiException(PurchaseErrorCodes.BUYER_WIFI_SERVICE_ALREADY_STARTED);
        if (purchase.getSessionFinishedAt() != null)
            throw new HotifiException(PurchaseErrorCodes.BUYER_WIFI_SERVICE_ALREADY_FINISHED);
        if (purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE <= BuyerPaymentCodes.PAYMENT_AUTHORIZED.value())
            throw new HotifiException(PurchaseErrorCodes.PAYMENT_NOT_SUCCESSFUL);
        try {
            Date sessionStartedAt = new Date(System.currentTimeMillis());
            purchase.setSessionCreatedAt(sessionStartedAt);
            purchase.setSessionModifiedAt(sessionStartedAt);
            purchase.setStatus(purchase.getStatus() - purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE + BuyerPaymentCodes.START_WIFI_SERVICE.value());
            purchaseRepository.save(purchase);
            saveSessionWithWifiService(purchase);
            return sessionStartedAt;
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(PurchaseErrorCodes.UNEXPECTED_PURCHASE_ERROR);
        }
    }

    @Transactional
    @Override
    public int updateBuyerWifiService(Long purchaseId, double dataUsed) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
        if (LegitUtils.isPurchaseUpdateLegit(purchase, dataUsed)) {
            int dataBought = purchase.getData();
            int purchaseStatus = purchase.getStatus() - purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE + BuyerPaymentCodes.UPDATE_WIFI_SERVICE.value();
            Date now = new Date(System.currentTimeMillis());

            double dataUsedBefore = purchase.getDataUsed();
            BigDecimal calculatedRefundAmount = calculateBuyerRefundAmount(dataBought, dataUsed, purchase.getAmountPaid());
            BigDecimal calculatedSellerAmount = calculateSellerPaymentAmount(dataBought, dataUsed, dataUsedBefore, purchase.getAmountPaid());

            //Updating seller payment each time update is made
            Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
            User seller = session != null ? session.getSpeedTest().getUser() : null;
            SellerPayment sellerPayment = seller != null ? sellerPaymentRepository.findSellerPaymentBySellerId(seller.getId()) : null;
            boolean isUpdateTimeOnly = Double.compare(dataUsed, purchase.getDataUsed()) == 0;

            if (sellerPayment == null)
                sellerPaymentService.addSellerPayment(seller, calculatedSellerAmount);
            else if (Double.compare(dataUsed, purchase.getDataUsed()) >= 0)
                sellerPaymentService.updateSellerPayment(seller, sellerPayment.getAmountEarned()
                        .add(calculatedSellerAmount), isUpdateTimeOnly);

            purchase.setDataUsed(dataUsed);
            purchase.setAmountRefund(calculatedRefundAmount);
            purchase.setSessionModifiedAt(now);
            purchase.setStatus(purchaseStatus);
            purchaseRepository.save(purchase);

            //Comparing if data is going to be exhausted
            if (Double.compare((double) dataBought - dataUsed, Constants.MINIMUM_DATA_THRESHOLD_MB) < 0) {
                log.info("Finish wifi service");
                return 2;
            }

            if (Double.compare(dataUsed, 0.9 * dataBought) > 0) {
                log.info("90% data consumed");
                return 1;
            }

            return 0;
        }

        return -1;
    }

    @Override
    @Transactional
    public WifiSummaryResponse finishBuyerWifiService(Long purchaseId, double dataUsed) {

        Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
        PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY);

        if (LegitUtils.isPurchaseUpdateLegit(purchase, dataUsed)) {
            int dataBought = purchase.getData();
            Date sessionFinishedAt = new Date(System.currentTimeMillis());
            double dataUsedBefore = purchase.getDataUsed();
            BigDecimal amountRefund = calculateBuyerRefundAmount(dataBought, dataUsed, purchase.getAmountPaid());
            BigDecimal sellerAmount = calculateSellerPaymentAmount(dataBought, dataUsed, dataUsedBefore, purchase.getAmountPaid());
            Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
            User seller = session != null ? session.getSpeedTest().getUser() : null;
            boolean isUpdateTimeOnly = Double.compare(dataUsed, purchase.getDataUsed()) == 0;

            SellerPayment sellerPayment = seller != null ? sellerPaymentRepository.findSellerPaymentBySellerId(seller.getId()) : null;
            if (sellerPayment == null)
                throw new HotifiException(PurchaseErrorCodes.UPDATE_WIFI_SERVICE_BEFORE_FINISHING);
            if (Double.compare(dataUsed, purchase.getDataUsed()) >= 0)
                sellerPaymentService.updateSellerPayment(seller, sellerPayment.getAmountEarned().add(sellerAmount), isUpdateTimeOnly);

            RefundReceiptResponse refundReceiptResponse = paymentProcessor.startBuyerRefund(purchaseRepository, amountRefund, purchase.getPaymentId());
            purchase.setStatus(refundReceiptResponse.getPurchase().getStatus());
            purchase.setDataUsed(dataUsed);
            purchase.setAmountRefund(amountRefund);
            purchase.setSessionFinishedAt(sessionFinishedAt);
            purchaseRepository.save(purchase);
            saveSessionWithWifiService(purchase);
            return getBuyerWifiSummary(purchase, false);
        }

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
    public void withdrawBuyerRefunds(Long buyerId) {
        User buyer = userRepository.findById(buyerId).orElse(null);
        if (LegitUtils.isBuyerLegit(buyer)) {
            PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY);
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Date currentTime = new Date(System.currentTimeMillis());
            Supplier<Stream<Purchase>> purchaseStreamSupplier = () -> purchaseRepository.findPurchasesByBuyerId(buyerId, pageable).stream();
            BigDecimal totalRefundAmount = purchaseStreamSupplier.get()
                    .filter(purchase -> purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE < BuyerPaymentCodes.REFUND_PENDING.value() && !PaymentUtils.isBuyerRefundDue(currentTime, purchase.getPaymentDoneAt()))
                    .map(Purchase::getAmountRefund)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalRefundAmount.compareTo(BigDecimal.ZERO) == 0)
                throw new HotifiException(PurchaseErrorCodes.BUYER_PENDING_REFUNDS_NOT_FOUND);

            List<Purchase> purchases = purchaseStreamSupplier.get()
                    .filter(purchase -> purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE < BuyerPaymentCodes.REFUND_PENDING.value()
                            && !PaymentUtils.isBuyerRefundDue(currentTime, purchase.getPaymentDoneAt()))
                    .collect(Collectors.toList());

            purchases.forEach(purchase -> {
                RefundReceiptResponse buyerRefund = paymentProcessor.startBuyerRefund(purchaseRepository, purchase.getAmountRefund(), purchase.getPaymentId());
                String refundPaymentId = buyerRefund.getPurchase().getRefundPaymentId();
                Date refundStartedAt = buyerRefund.getPurchase().getRefundStartedAt();
                String refundTransactionId = buyerRefund.getPurchase().getRefundTransactionId();
                int buyerPaymentStatus = buyerRefund.getPurchase().getStatus();
                purchaseRepository.updatePurchaseRefundStatus(buyerPaymentStatus, refundPaymentId, refundStartedAt, refundTransactionId, purchase.getId());
            });

        } else
            throw new HotifiException(PurchaseErrorCodes.PURCHASE_UPDATE_NOT_LEGIT);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RefundReceiptResponse> getBuyerRefundReceipts(Long buyerId, int page, int size, boolean isDescending) {
        User buyer = userRepository.findById(buyerId).orElse(null);
        if (LegitUtils.isBuyerLegit(buyer)) {
            Pageable pageable = isDescending ? PageRequest.of(page, size, Sort.by("refund_done_at").descending()) :
                    PageRequest.of(page, size, Sort.by("refund_done_at"));
            List<Purchase> purchaseReceipts = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable)
                    .stream()
                    .filter(purchase -> purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.REFUND_PENDING.value())
                    .collect(Collectors.toList());
            List<RefundReceiptResponse> refundReceiptResponses = new ArrayList<>();
            PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY);
            purchaseReceipts.forEach(purchase -> {
                /*The below case will arise only on instant refunds or payments older than 6 months
                if (purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE == BuyerPaymentCodes.REFUND_FAILED.value()) {

                }*/
                RefundReceiptResponse receiptResponse = paymentProcessor.getBuyerRefundStatus(purchaseRepository, purchase.getPaymentId(), true);
                //Setting up values from payment processor
                int status = receiptResponse.getPurchase().getStatus();
                Date refundDoneAt = receiptResponse.getPurchase().getRefundDoneAt();
                String refundPaymentId = receiptResponse.getPurchase().getRefundPaymentId();
                String refundTransactionId = receiptResponse.getPurchase().getRefundTransactionId();
                purchaseRepository.updatePurchaseRefundStatus(status, refundPaymentId, refundDoneAt, refundTransactionId, purchase.getId());

                purchase.setStatus(status);
                purchase.setRefundPaymentId(refundPaymentId);
                purchase.setRefundDoneAt(refundDoneAt);
                purchase.setRefundTransactionId(refundTransactionId);
                RefundReceiptResponse refundReceiptResponse = new RefundReceiptResponse(purchase, Constants.HOTIFI_BANK_ACCOUNT);
                refundReceiptResponses.add(refundReceiptResponse);
            });
            return refundReceiptResponses;
        }

        throw new HotifiException(PurchaseErrorCodes.BUYER_NOT_LEGIT);
    }

    //User defined functions
    private BigDecimal calculateBuyerRefundAmount(double dataBought, double dataUsed, BigDecimal amountPaid) {
        //Do not check for error types, simply calculate and return refund amount
        //return amountPaid - Math.ceil((amountPaid / dataBought) * dataUsed);
        return amountPaid
                .subtract
                        (PaymentUtils
                                .divideThenMultiplyFloorZeroScale(amountPaid, BigDecimal.valueOf(dataBought), BigDecimal.valueOf(dataUsed)));
    }

    private BigDecimal calculateSellerPaymentAmount(double dataBought, double dataUsed, double dataUsedBefore, BigDecimal amountPaid) {
        //Do not check for error types, simply calculate and return refund amount
        //return (amountPaid / dataBought) * (dataUsed - dataUsedBefore);
        return PaymentUtils
                .divideThenMultiplyFloorTwoScale(amountPaid, BigDecimal.valueOf(dataBought), BigDecimal.valueOf(dataUsed - dataUsedBefore));
    }

    private WifiSummaryResponse getBuyerWifiSummary(Purchase purchase, boolean isWithdrawAmount) {
        Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
        SpeedTest speedTest = session != null ? speedTestRepository.findById(session.getSpeedTest().getId()).orElse(null) : null;
        User seller = speedTest != null ? userRepository.findById(speedTest.getUser().getId()).orElse(null) : null;

        if (seller == null)
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);

        PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY);

        RefundReceiptResponse refundReceiptResponse = isWithdrawAmount ? paymentProcessor.getBuyerRefundStatus(purchaseRepository, purchase.getPaymentId(), true) : paymentProcessor.getBuyerRefundStatus(purchaseRepository, purchase.getPaymentId(), false);
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

        //Below condition will save the entity only if complete payment with refund has not completed yet
        if (refundReceiptResponse.getPurchase().getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE < BuyerPaymentCodes.values().length)
            purchaseRepository.save(refundReceiptResponse.getPurchase());

        return wifiSummaryResponse;
    }

    private void saveSessionWithWifiService(Purchase purchase) {
        Session session = sessionRepository.findById(purchase.getSession().getId()).orElse(null);
        if (session == null)
            throw new HotifiException(PurchaseErrorCodes.SESSION_NOT_FOUND);
        session.setDataUsed(PaymentUtils.getDataUsedSumOfSession(session));
        session.setModifiedAt(purchase.getSessionModifiedAt());
        sessionRepository.save(session);
    }

    private List<WifiSummaryResponse> getWifiSummaryResponses(Long buyerId, Pageable pageable) {
        List<Purchase> purchases = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable);
        List<WifiSummaryResponse> wifiSummaryResponses = new ArrayList<>();
        for (Purchase purchase : purchases) {
            WifiSummaryResponse wifiSummaryResponse = getBuyerWifiSummary(purchase, true);
            wifiSummaryResponses.add(wifiSummaryResponse);
        }
        return wifiSummaryResponses;
    }

}