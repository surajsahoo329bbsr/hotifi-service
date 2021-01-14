package com.api.hotifi.session.service;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.utils.AESUtils;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.Device;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.error.PurchaseErrorCodes;
import com.api.hotifi.payment.processor.codes.BuyerPaymentCodes;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.repositories.SellerPaymentRepository;
import com.api.hotifi.payment.services.interfaces.IFeedbackService;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.error.SessionErrorCodes;
import com.api.hotifi.session.model.Buyer;
import com.api.hotifi.session.repository.SessionRepository;
import com.api.hotifi.session.web.request.SessionRequest;
import com.api.hotifi.session.web.response.ActiveSessionsResponse;
import com.api.hotifi.session.web.response.SessionSummaryResponse;
import com.api.hotifi.speed_test.entity.SpeedTest;
import com.api.hotifi.speed_test.repository.SpeedTestRepository;
import com.api.hotifi.speed_test.service.ISpeedTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SessionServiceImpl implements ISessionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpeedTestRepository speedTestRepository;

    @Autowired
    private ISpeedTestService speedTestService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SellerPaymentRepository sellerPaymentRepository;

    @Autowired
    private IFeedbackService feedbackService;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Transactional
    @Override
    public Session addSession(SessionRequest sessionRequest) {

        User user = userRepository.findById(sessionRequest.getUserId()).orElse(null);
        if (!LegitUtils.isSellerLegit(user))
            throw new HotifiException(SessionErrorCodes.SELLER_NOT_LEGIT);

        SpeedTest speedTest = speedTestService.getLatestSpeedTest(sessionRequest.getUserId(), sessionRequest.getPinCode(), sessionRequest.isWifi());
        if (speedTest == null && sessionRequest.isWifi())
            throw new HotifiException(SessionErrorCodes.WIFI_SPEED_TEST_ABSENT);
        if (speedTest == null && !sessionRequest.isWifi())
            throw new HotifiException(SessionErrorCodes.SPEED_TEST_ABSENT);

        SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(user.getId());
        if (sellerPayment != null && Double.compare(sellerPayment.getAmountEarned(), Constants.MAXIMUM_SELLER_AMOUNT_EARNED) > 0)
            throw new HotifiException(SessionErrorCodes.WITHDRAW_SELLER_AMOUNT);

        try {
            //Do not check if user is banned or freezed, because that checking is for buyers only
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            List<Long> speedTestIds = speedTestRepository.findSpeedTestsByUserId(sessionRequest.getUserId(), pageable)
                    .stream()
                    .map(SpeedTest::getId)
                    .collect(Collectors.toList());

            //speedTestIds.forEach(id -> log.info("Id : "+ id));

            Date finishedAt = new Date(System.currentTimeMillis());
            sessionRepository.updatePreviousSessionsFinishTimeIfNull(speedTestIds, finishedAt);

            //Everything is fine, so encrypt the password
            String encryptedString = AESUtils.encrypt(sessionRequest.getWifiPassword(), Constants.WIFI_PASSWORD_SECRET_KEY);
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

                sessions.forEach((seller, session) -> {
                    double availableData = session.getData() - session.getDataUsed();
                    double availableDataPrice = (session.getPrice() / session.getData()) * availableData;
                    double downloadSpeed = session.getSpeedTest().getDownloadSpeed();
                    double uploadSpeed = session.getSpeedTest().getUploadSpeed();
                    ActiveSessionsResponse activeSessionsResponse = new ActiveSessionsResponse();
                    activeSessionsResponse.setSessionId(session.getId());
                    activeSessionsResponse.setSessionId(seller.getId());
                    activeSessionsResponse.setUsername(seller.getUser().getUsername());
                    activeSessionsResponse.setUserPhotoUrl(seller.getUser().getPhotoUrl());
                    activeSessionsResponse.setData(availableData);
                    activeSessionsResponse.setPrice(availableDataPrice);
                    activeSessionsResponse.setAverageRating(feedbackService.getAverageRating(seller.getId()));
                    activeSessionsResponse.setDownloadSpeed(downloadSpeed);
                    activeSessionsResponse.setUploadSpeed(uploadSpeed);
                    activeSessionsResponses.add(activeSessionsResponse);
                });
            });
            return activeSessionsResponses;
        } catch (Exception e) {
            log.error("Error Occured ", e);
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    @Override
    public List<Buyer> getBuyers(Long sessionId, boolean isActive) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null)
            throw new HotifiException(PurchaseErrorCodes.NO_SESSION_EXISTS);
        if (session.getFinishedAt() != null && isActive)
            throw new HotifiException(PurchaseErrorCodes.SESSION_ALREADY_ENDED);
        try {
            List<Buyer> buyers = new ArrayList<>();
            List<Purchase> purchases = isActive ?
                    purchaseRepository.findPurchasesBySessionIds(Collections.singletonList(session.getId()))
                            .stream()
                            .filter(purchase -> purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE >= BuyerPaymentCodes.START_WIFI_SERVICE.value() && purchase.getSessionFinishedAt() == null)
                            .collect(Collectors.toList()) :
                    purchaseRepository.findPurchasesBySessionIds(Collections.singletonList(session.getId()))
                            .stream()
                            .filter(purchase -> purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE >= BuyerPaymentCodes.START_WIFI_SERVICE.value())
                            .collect(Collectors.toList());
            purchases.forEach(purchase -> {
                Buyer buyer = new Buyer();
                buyer.setUsername(purchase.getUser().getUsername());
                buyer.setPhotoUrl(purchase.getUser().getPhotoUrl());
                buyer.setMacAddress(purchase.getMacAddress());
                buyer.setSessionCreatedAt(purchase.getSessionCreatedAt());
                buyer.setSessionModifiedAt(purchase.getSessionModifiedAt());
                buyer.setSessionFinishedAt(purchase.getSessionFinishedAt());
                buyer.setAmountPaid(purchase.getAmountPaid() - purchase.getAmountRefund());
                buyer.setDataBought(purchase.getData());
                buyer.setDataUsed(purchase.getDataUsed());
                buyers.add(buyer);
            });
            return buyers;
        } catch (Exception e) {
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    @Override
    public void sendNotificationsToFinishSession(Long sessionId) {
        //TODO send notification to all connected devices to finish wifi
        Set<String> usernames = getBuyers(sessionId, true)
                .stream().map(Buyer::getUsername)
                .collect(Collectors.toSet());
        List<User> buyers = userRepository.findAllUsersByUsernames(usernames);
        List<String> deviceTokens = new ArrayList<>();
        buyers.forEach(buyer -> {
            Set<Device> devices = buyer.getUserDevices();
            devices.forEach(device -> deviceTokens.add(device.getToken()));
        });

        log.info("Tokens" + deviceTokens);
        //TODO fetch device tokens

    }

    @Override
    public SessionSummaryResponse getSessionSummary(Long sessionId, boolean isForceStop) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        List<Buyer> buyers = getBuyers(sessionId, true);
        if (session == null)
            throw new HotifiException(PurchaseErrorCodes.NO_SESSION_EXISTS);
        if (!isForceStop && buyers != null && buyers.size() > 0)
            throw new HotifiException(SessionErrorCodes.NOTIFY_BUYERS_TO_FINISH_SESSION);
        try {
            double totalEarnings = purchaseRepository.findPurchasesBySessionIds(Collections.singletonList(sessionId))
                    .stream()
                    .filter(purchase -> purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE >= BuyerPaymentCodes.START_WIFI_SERVICE.value())
                    .mapToDouble(purchase -> purchase.getAmountPaid() - purchase.getAmountRefund())
                    .sum();
            int netEarnings = (int) Math.ceil(totalEarnings * (double) (100 - Constants.COMMISSION_PERCENTAGE) / 100);
            SessionSummaryResponse sessionSummaryResponse = new SessionSummaryResponse();
            sessionSummaryResponse.setSessionCreatedAt(session.getCreatedAt());
            sessionSummaryResponse.setSessionModifiedAt(session.getModifiedAt());
            sessionSummaryResponse.setSessionFinishedAt(session.getFinishedAt());
            sessionSummaryResponse.setBuyers(getBuyers(sessionId, false));
            sessionSummaryResponse.setTotalData(session.getData());
            sessionSummaryResponse.setTotalDataSold(session.getDataUsed());
            sessionSummaryResponse.setTotalEarnings(totalEarnings);
            sessionSummaryResponse.setNetEarnings(netEarnings);
            return sessionSummaryResponse;
        } catch (Exception e) {
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<SessionSummaryResponse> getSortedSessionsByStartTime(Long userId, int page, int size, boolean isDescending) {
        try {
            List<SpeedTest> speedTests = speedTestService.getSortedTestByDateTime(userId, 0, Integer.MAX_VALUE, isDescending);
            List<Long> speedTestIds = speedTests
                    .stream()
                    .map(SpeedTest::getId)
                    .collect(Collectors.toList());

            Pageable sortedPageableByStartTime
                    = isDescending ?
                    PageRequest.of(page, size, Sort.by("start_time").descending())
                    : PageRequest.of(page, size, Sort.by("start_time"));

            return getSummaryResponses(speedTestIds, sortedPageableByStartTime);

        } catch (Exception e) {
            log.error("Exception ", e);
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<SessionSummaryResponse> getSortedSessionsByDataUsed(Long userId, int page, int size, boolean isDescending) {
        try {
            List<SpeedTest> speedTests = speedTestService.getSortedTestByDateTime(userId, 0, Integer.MAX_VALUE, isDescending);
            List<Long> speedTestIds = speedTests
                    .stream()
                    .map(SpeedTest::getId)
                    .collect(Collectors.toList());

            Pageable sortedPageableByDataUsed
                    = isDescending ?
                    PageRequest.of(page, size, Sort.by("data_used").descending())
                    : PageRequest.of(page, size, Sort.by("data_used"));

            return getSummaryResponses(speedTestIds, sortedPageableByDataUsed);
        } catch (Exception e) {
            log.error("Exception ", e);
            throw new HotifiException(SessionErrorCodes.UNEXPECTED_SESSION_ERROR);
        }
    }

    public List<SessionSummaryResponse> getSummaryResponses(List<Long> speedTestIds, Pageable pageable) {
        List<Session> sessions = sessionRepository.findAllSessionsById(speedTestIds, pageable);
        List<SessionSummaryResponse> sessionSummaryResponses = new ArrayList<>();
        sessions.forEach(session -> {
            double totalEarnings = purchaseRepository.findPurchasesBySessionIds(Collections.singletonList(session.getId()))
                    .stream()
                    .filter(purchase -> purchase.getStatus() % Constants.BUYER_PAYMENT_START_VALUE_CODE >= BuyerPaymentCodes.START_WIFI_SERVICE.value())
                    .mapToDouble(purchase -> purchase.getAmountPaid() - purchase.getAmountRefund())
                    .sum();
            int netEarnings = (int) Math.ceil(totalEarnings * (double) (100 - Constants.COMMISSION_PERCENTAGE) / 100);
            SessionSummaryResponse sessionSummaryResponse = new SessionSummaryResponse();
            sessionSummaryResponse.setSessionCreatedAt(session.getCreatedAt());
            sessionSummaryResponse.setSessionModifiedAt(session.getModifiedAt());
            sessionSummaryResponse.setSessionFinishedAt(session.getFinishedAt());
            sessionSummaryResponse.setBuyers(getBuyers(session.getId(), false));
            sessionSummaryResponse.setTotalData(session.getData());
            sessionSummaryResponse.setTotalDataSold(session.getDataUsed());
            sessionSummaryResponse.setTotalEarnings(totalEarnings);
            sessionSummaryResponse.setNetEarnings(netEarnings);
            sessionSummaryResponses.add(sessionSummaryResponse);
        });

        return sessionSummaryResponses;
    }

}
