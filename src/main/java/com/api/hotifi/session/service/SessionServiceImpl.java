package com.api.hotifi.session.service;

import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.processors.codes.CloudClientCodes;
import com.api.hotifi.common.services.interfaces.INotificationService;
import com.api.hotifi.common.utils.AESUtils;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.models.RoleName;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.services.interfaces.IUserStatusService;
import com.api.hotifi.identity.web.request.UserStatusRequest;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.error.PurchaseErrorCodes;
import com.api.hotifi.payment.processor.codes.BuyerPaymentCodes;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.repositories.SellerPaymentRepository;
import com.api.hotifi.payment.services.interfaces.IFeedbackService;
import com.api.hotifi.payment.utils.PaymentUtils;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.error.SessionErrorCodes;
import com.api.hotifi.session.model.Buyer;
import com.api.hotifi.session.repository.SessionRepository;
import com.api.hotifi.session.web.request.SessionRequest;
import com.api.hotifi.session.web.response.ActiveSessionsResponse;
import com.api.hotifi.session.web.response.SessionSummaryResponse;
import com.api.hotifi.speedtest.entity.SpeedTest;
import com.api.hotifi.speedtest.repository.SpeedTestRepository;
import com.api.hotifi.speedtest.service.ISpeedTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class SessionServiceImpl implements ISessionService {

    private final UserRepository userRepository;
    private final SpeedTestRepository speedTestRepository;
    private final SessionRepository sessionRepository;
    private final SellerPaymentRepository sellerPaymentRepository;
    private final PurchaseRepository purchaseRepository;
    private final ISpeedTestService speedTestService;
    private final IFeedbackService feedbackService;
    private final IUserStatusService userStatusService;
    private final INotificationService notificationService;

    public SessionServiceImpl(UserRepository userRepository, SpeedTestRepository speedTestRepository, SessionRepository sessionRepository, SellerPaymentRepository sellerPaymentRepository, PurchaseRepository purchaseRepository, ISpeedTestService speedTestService, IFeedbackService feedbackService, IUserStatusService userStatusService, INotificationService notificationService) {
        this.userRepository = userRepository;
        this.speedTestRepository = speedTestRepository;
        this.sessionRepository = sessionRepository;
        this.sellerPaymentRepository = sellerPaymentRepository;
        this.purchaseRepository = purchaseRepository;
        this.speedTestService = speedTestService;
        this.feedbackService = feedbackService;
        this.userStatusService = userStatusService;
        this.notificationService = notificationService;
    }

    @Transactional
    @Override
    public Session addSession(SessionRequest sessionRequest) {

        User user = userRepository.findById(sessionRequest.getUserId()).orElse(null);
        boolean isSellerLegit = AppConfigurations.DIRECT_TRANSFER_API_ENABLED ?
                LegitUtils.isSellerLegit(user, false) : LegitUtils.isSellerUpiLegit(user, false);
        if (!isSellerLegit)
            throw new HotifiException(SessionErrorCodes.SELLER_NOT_LEGIT);

        SpeedTest speedTest = speedTestService.getLatestSpeedTest(sessionRequest.getUserId(), sessionRequest.getPinCode(), sessionRequest.isWifi());
        if (speedTest == null && sessionRequest.isWifi())
            throw new HotifiException(SessionErrorCodes.WIFI_SPEED_TEST_ABSENT);
        if (speedTest == null && !sessionRequest.isWifi())
            throw new HotifiException(SessionErrorCodes.SPEED_TEST_ABSENT);

        SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(user.getId());
        if (sellerPayment != null && sellerPayment.getAmountEarned().compareTo(BigDecimal.valueOf(BusinessConfigurations.MAXIMUM_SELLER_AMOUNT_EARNED)) > 0)
            throw new HotifiException(SessionErrorCodes.WITHDRAW_SELLER_AMOUNT);

        try {
            //Do not check if user is banned or freezed, because that checking is for buyers only
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            List<Long> speedTestIds = speedTestRepository.findSpeedTestsByUserId(sessionRequest.getUserId(), pageable)
                    .stream()
                    .map(SpeedTest::getId)
                    .collect(Collectors.toList());

            Date finishedAt = new Date(System.currentTimeMillis());
            sessionRepository.updatePreviousSessionsFinishTimeIfNull(speedTestIds, finishedAt);

            //Everything is fine, so encrypt the password
            String encryptedString = AESUtils.encrypt(sessionRequest.getWifiPassword(), BusinessConfigurations.AES_PASSWORD_SECRET_KEY);
            Session session = new Session();
            session.setSpeedTest(speedTest);
            session.setData(sessionRequest.getData());
            session.setWifiPassword(encryptedString);
            session.setPrice(sessionRequest.getPrice());
            return sessionRepository.save(session);
        } catch (Exception e) {
            log.error("Error Occurred ", e);
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    @Transactional
    @Override
    public List<ActiveSessionsResponse> getActiveSessions(Set<String> usernames) {
        try {
            List<User> users = userRepository.findAllUsersByUsernames(usernames);
            //users.forEach(s -> log.info("username : "+s.getUsername()));
            List<Long> userIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("created_at").descending());
            Map<Long, List<SpeedTest>> speedTests = speedTestRepository.findSpeedTestsByUserIds(userIds, pageable)
                    .stream()
                    .collect(Collectors.groupingBy(speedTest -> speedTest.getUser().getId()));
            List<ActiveSessionsResponse> activeSessionsResponses = new ArrayList<>();

            speedTests.forEach((sellerId, speedTestList) -> {
                //Long sellerId = entry.getKey();
                List<Long> speedTestIds = speedTestList.stream()
                        .map(SpeedTest::getId)
                        .collect(Collectors.toList());

                Map<SpeedTest, Session> sessions = sessionRepository.findActiveSessionsBySpeedTestIds(speedTestIds)
                        .stream()
                        .collect(Collectors.toMap(Session::getSpeedTest, Function.identity(),
                                BinaryOperator.maxBy(Comparator.comparing(Session::getId))));

                sessions.forEach((speedTest, session) -> {
                    double availableData = session.getData() - session.getDataUsed();
                    //BigDecimal availableDataPrice = PaymentUtils
                    //.divideThenMultiplyCeilingZeroScale(session.getPrice(), BigDecimal.valueOf(BusinessConfigurations.UNIT_GB_VALUE_IN_MB), BigDecimal.valueOf(availableData));
                    double downloadSpeed = session.getSpeedTest().getDownloadSpeed();
                    double uploadSpeed = session.getSpeedTest().getUploadSpeed();
                    ActiveSessionsResponse activeSessionsResponse = new ActiveSessionsResponse();
                    activeSessionsResponse.setSessionId(session.getId());
                    activeSessionsResponse.setSellerId(speedTest.getUser().getId());
                    activeSessionsResponse.setUsername(speedTest.getUser().getUsername());
                    activeSessionsResponse.setUserPhotoUrl(speedTest.getUser().getPhotoUrl());
                    activeSessionsResponse.setNetworkProvider(speedTest.getNetworkProvider());
                    activeSessionsResponse.setData(availableData);
                    activeSessionsResponse.setPrice(session.getPrice());
                    activeSessionsResponse.setAverageRating(feedbackService.getAverageRating(speedTest.getUser().getId()));
                    activeSessionsResponse.setDownloadSpeed(downloadSpeed);
                    activeSessionsResponse.setUploadSpeed(uploadSpeed);
                    activeSessionsResponses.add(activeSessionsResponse);
                });
            });
            return activeSessionsResponses;
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    @Override
    public List<Buyer> getBuyers(Long sessionId, boolean isActive) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null)
            throw new HotifiException(PurchaseErrorCodes.SESSION_NOT_FOUND);
        if (session.getFinishedAt() != null && isActive)
            throw new HotifiException(PurchaseErrorCodes.SESSION_ALREADY_ENDED);
        try {
            List<Buyer> buyers = new ArrayList<>();
            List<Purchase> purchases = isActive ?
                    purchaseRepository.findPurchasesBySessionIds(Collections.singletonList(session.getId()))
                            .stream()
                            .filter(purchase -> purchase.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.START_WIFI_SERVICE.value() && purchase.getSessionFinishedAt() == null)
                            .collect(Collectors.toList()) :
                    purchaseRepository.findPurchasesBySessionIds(Collections.singletonList(session.getId()))
                            .stream()
                            .filter(purchase -> purchase.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.START_WIFI_SERVICE.value())
                            .collect(Collectors.toList());
            purchases.forEach(purchase -> {
                Buyer buyer = new Buyer();
                buyer.setUsername(purchase.getUser().getUsername());
                buyer.setPhotoUrl(purchase.getUser().getPhotoUrl());
                buyer.setMacAddress(purchase.getMacAddress());
                buyer.setIpAddress(purchase.getIpAddress());
                buyer.setStatus(purchase.getStatus());
                buyer.setPaidAt(purchase.getPaymentDoneAt());
                buyer.setSessionModifiedAt(purchase.getSessionModifiedAt());
                buyer.setSessionFinishedAt(purchase.getSessionFinishedAt());
                buyer.setAmountPaid(purchase.getAmountPaid().subtract(purchase.getAmountRefund()));
                buyer.setDataBought(purchase.getData());
                buyer.setDataUsed(purchase.getDataUsed());
                buyers.add(buyer);

                Date currentTime = new Date(System.currentTimeMillis());
                Date lastModifiedAt = purchase.getSessionModifiedAt() != null ? purchase.getSessionModifiedAt() : purchase.getSessionCreatedAt();
                //Add user status abnormal activities logic
                if (PaymentUtils.isAbnormalBehaviour(currentTime, lastModifiedAt) && isActive) {
                    UserStatusRequest userStatusRequest = new UserStatusRequest();
                    userStatusRequest.setUserId(purchase.getUser().getId());
                    userStatusRequest.setWarningReason("Abnormal Activity");
                    userStatusRequest.setPurchaseId(purchase.getId());
                    userStatusRequest.setRole(RoleName.CUSTOMER.name());
                    userStatusService.addUserStatus(userStatusRequest);
                }

            });
            return buyers;
        } catch (Exception e) {
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    @Override
    public void sendNotificationsToFinishSession(Long sessionId) {
        Set<String> usernames = getBuyers(sessionId, true)
                .stream().map(Buyer::getUsername)
                .collect(Collectors.toSet());
        Session session = sessionRepository.findById(sessionId).orElse(null);
        User seller = session != null ? session.getSpeedTest().getUser() : null;
        String sellerPhotoUrl = seller != null ? seller.getPhotoUrl() : null;
        String sellerUsername = seller != null ? seller.getUsername() : null;
        List<Long> buyers = userRepository.findAllUsersByUsernames(usernames)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
        notificationService.sendPhotoNotificationsToMultipleUsers(buyers, sellerUsername + " will stop wifi service", "Please finish your work in " + BusinessConfigurations.SELLER_SESSION_CLOSE_WAIT_TIME_MINUTES + " minutes", sellerPhotoUrl, CloudClientCodes.GOOGLE_CLOUD_PLATFORM);
    }

    @Transactional
    @Override
    public void finishSession(Long sessionId, boolean isForceStop) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        List<Buyer> buyers = getBuyers(sessionId, true);
        if (session == null)
            throw new HotifiException(PurchaseErrorCodes.SESSION_NOT_FOUND);
        if (session.getFinishedAt() != null)
            throw new HotifiException(SessionErrorCodes.SESSION_ALREADY_FINISHED);
        //TODO TBC if preference to be given to seller or buyer, if buyer has not closed wifi session even after warning
        // has been sent. For now preference has been given to seller,after sending a notification
        /*if (!isForceStop && buyers != null && buyers.size() > 0)
            throw new HotifiException(SessionErrorCodes.NOTIFY_BUYERS_TO_FINISH_SESSION);*/

        User buyer = userRepository.findById(session.getSpeedTest().getUser().getId()).orElse(null);
        if (LegitUtils.isBuyerLegit(buyer)) {
            session.setFinishedAt(new Date(System.currentTimeMillis()));
            sessionRepository.save(session);
            User seller = session.getSpeedTest().getUser();
            notificationService.sendNotificationToSingleUser(seller.getId(), "Hotspot Stopped", "Wifi Hotspot Stopped", CloudClientCodes.GOOGLE_CLOUD_PLATFORM);
        } else {
            throw new HotifiException(UserErrorCodes.USER_NOT_LEGIT);
        }
    }

    @Transactional
    @Override
    public BigDecimal calculatePaymentForDataToBeUsed(Long sessionId, int dataToBeUsed) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null)
            throw new HotifiException(PurchaseErrorCodes.SESSION_NOT_FOUND);
        double availableData = session.getData() - session.getDataUsed();
        if (Double.compare(dataToBeUsed, availableData) > 0)
            throw new HotifiException(PurchaseErrorCodes.EXCESS_DATA_TO_BUY_ERROR);
        //return Math.ceil(session.getPrice() / Constants.UNIT_GB_VALUE_IN_MB * dataToBeUsed);
        return PaymentUtils.divideThenMultiplyCeilingZeroScale(session.getPrice(), BigDecimal.valueOf(BusinessConfigurations.UNIT_GB_VALUE_IN_MB), BigDecimal.valueOf(dataToBeUsed));
    }

    @Override
    public SessionSummaryResponse getSessionSummary(Long sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null)
            throw new HotifiException(PurchaseErrorCodes.SESSION_NOT_FOUND);
        try {
            Date finishedAt = new Date(System.currentTimeMillis());
            List<Buyer> buyers = getBuyers(sessionId, false);
            BigDecimal totalEarnings = purchaseRepository.findPurchasesBySessionIds(Collections.singletonList(sessionId))
                    .stream()
                    .filter(purchase -> purchase.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.START_WIFI_SERVICE.value())
                    .map(purchase -> PaymentUtils.divideThenMultiplyFloorTwoScale(purchase.getAmountPaid(),
                            BigDecimal.valueOf(purchase.getData()), BigDecimal.valueOf(purchase.getDataUsed())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            double totalDataSold = (double) Math.round(purchaseRepository.findPurchasesBySessionIds(Collections.singletonList(sessionId))
                    .stream()
                    .mapToDouble(Purchase::getDataUsed)
                    .sum() * 100) / 100;
            BigDecimal netEarnings = totalEarnings
                    .multiply(BigDecimal.valueOf((double) (100 - BusinessConfigurations.COMMISSION_PERCENTAGE) / 100))
                    .setScale(2, RoundingMode.FLOOR);
            SessionSummaryResponse sessionSummaryResponse = new SessionSummaryResponse();
            sessionSummaryResponse.setSessionCreatedAt(session.getCreatedAt());
            sessionSummaryResponse.setSessionModifiedAt(session.getModifiedAt());
            sessionSummaryResponse.setSessionFinishedAt(finishedAt);
            sessionSummaryResponse.setBuyers(buyers);
            sessionSummaryResponse.setTotalData(session.getData());
            sessionSummaryResponse.setTotalDataSold(totalDataSold);
            sessionSummaryResponse.setTotalEarnings(totalEarnings);
            sessionSummaryResponse.setNetworkProvider(session.getSpeedTest().getNetworkProvider());
            sessionSummaryResponse.setUnitSellingPrice(session.getPrice());
            sessionSummaryResponse.setNetEarnings(netEarnings);
            return sessionSummaryResponse;
        } catch (Exception e) {
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    @Transactional
    @Override
    public List<SessionSummaryResponse> getSortedSessionsByDateTime(Long sellerId, int page, int size, boolean isDescending) {
        try {
            List<SpeedTest> speedTests = speedTestService.getSortedSpeedTestByDateTime(sellerId, 0, Integer.MAX_VALUE, isDescending);
            List<Long> speedTestIds = speedTests
                    .stream()
                    .map(SpeedTest::getId)
                    .collect(Collectors.toList());

            Pageable sortedPageableByStartTime
                    = isDescending ?
                    PageRequest.of(page, size, Sort.by("created_at").descending())
                    : PageRequest.of(page, size, Sort.by("created_at"));

            return getSessionSummaryResponses(speedTestIds, sortedPageableByStartTime);

        } catch (Exception e) {
            log.error("Exception ", e);
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    @Transactional
    @Override
    public List<SessionSummaryResponse> getSortedSessionsByDataShared(Long sellerId, int page, int size, boolean isDescending) {
        try {
            List<SpeedTest> speedTests = speedTestService.getSortedSpeedTestByDateTime(sellerId, 0, Integer.MAX_VALUE, isDescending);
            List<Long> speedTestIds = speedTests
                    .stream()
                    .map(SpeedTest::getId)
                    .collect(Collectors.toList());

            Pageable sortedPageableByDataUsed
                    = isDescending ?
                    PageRequest.of(page, size, Sort.by("data").descending())
                    : PageRequest.of(page, size, Sort.by("data"));

            return getSessionSummaryResponses(speedTestIds, sortedPageableByDataUsed);
        } catch (Exception e) {
            log.error("Exception ", e);
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    public List<SessionSummaryResponse> getSessionSummaryResponses(List<Long> speedTestIds, Pageable pageable) {
        List<Session> sessions = sessionRepository.findSessionsBySpeedTestIds(speedTestIds, pageable);
        List<SessionSummaryResponse> sessionSummaryResponses = new ArrayList<>();
        sessions.forEach(session -> {
            BigDecimal totalEarnings = purchaseRepository.findPurchasesBySessionIds(Collections.singletonList(session.getId()))
                    .stream()
                    .filter(purchase -> purchase.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.START_WIFI_SERVICE.value())
                    .map(purchase -> PaymentUtils.divideThenMultiplyFloorTwoScale(purchase.getAmountPaid(),
                            BigDecimal.valueOf(purchase.getData()), BigDecimal.valueOf(purchase.getDataUsed())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            double totalDataSold = (double)
                    Math.round(purchaseRepository.findPurchasesBySessionIds(Collections.singletonList(session.getId()))
                            .stream()
                            .mapToDouble(Purchase::getDataUsed)
                            .sum() * 100) / 100;

            BigDecimal netEarnings = totalEarnings
                    .multiply(BigDecimal.valueOf((double) (100 - BusinessConfigurations.COMMISSION_PERCENTAGE) / 100));

            SessionSummaryResponse sessionSummaryResponse = new SessionSummaryResponse();
            sessionSummaryResponse.setSessionCreatedAt(session.getCreatedAt());
            sessionSummaryResponse.setSessionModifiedAt(session.getModifiedAt());
            sessionSummaryResponse.setSessionFinishedAt(session.getFinishedAt());
            sessionSummaryResponse.setBuyers(getBuyers(session.getId(), false));
            sessionSummaryResponse.setTotalData(session.getData());
            sessionSummaryResponse.setTotalDataSold(totalDataSold);
            sessionSummaryResponse.setNetworkProvider(session.getSpeedTest().getNetworkProvider());
            sessionSummaryResponse.setTotalEarnings(totalEarnings);
            sessionSummaryResponse.setUnitSellingPrice(session.getPrice());
            sessionSummaryResponse.setNetEarnings(netEarnings);
            sessionSummaryResponses.add(sessionSummaryResponse);
        });

        return sessionSummaryResponses;
    }

}
