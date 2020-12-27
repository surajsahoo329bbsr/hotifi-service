package com.api.hotifi.session.service;

import com.api.hotifi.common.utils.AESUtils;
import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.repositories.SellerPaymentRepository;
import com.api.hotifi.payment.services.interfaces.IFeedbackService;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.repository.SessionRepository;
import com.api.hotifi.session.web.request.SessionRequest;
import com.api.hotifi.session.web.response.ActiveSessionsResponse;
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

    @Transactional
    @Override
    public void addSession(SessionRequest sessionRequest) {
        try {
            //Do not check if user is banned or freezed, because that checking is for buyers only
            User user = userRepository.getOne(sessionRequest.getUserId());
            if (!LegitUtils.isSellerLegit(user))
                throw new Exception("Seller not legit to be updated");

            SpeedTest speedTest = speedTestService.getLatestSpeedTest(sessionRequest.getUserId(), sessionRequest.getPinCode(), sessionRequest.isWifi());
            if (speedTest == null && sessionRequest.isWifi())
                throw new Exception("User has not done wifi speed test");
            if (speedTest == null && !sessionRequest.isWifi())
                throw new Exception("User has not done speed test");

            SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(user.getId());
            if(Double.compare(sellerPayment.getAmountEarned(), Constants.MAXIMUM_SELLER_AMOUNT_EARNED) > 0)
                throw new Exception("Please withdraw amount before starting this session");

            //Everything is fine, so encrypt the password
            String encryptedString = AESUtils.encrypt(sessionRequest.getWifiPassword(), Constants.WIFI_PASSWORD_SECRET_KEY);

            Session session = new Session();
            session.setSpeedTest(speedTest);
            session.setData(sessionRequest.getData());
            session.setWifiPassword(encryptedString);
            session.setPrice(sessionRequest.getPrice());

            sessionRepository.save(session);

        } catch (Exception e) {
            log.error("Error Occurred ", e);
        }
    }

    @Transactional
    @Override
    public List<ActiveSessionsResponse> getActiveSessions(HashSet<String> usernames) {
        try {
            List<User> users = userRepository.findAllUsersByUsernames(usernames);
            List<Long> userIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("created_at").descending());
            Map<Long, List<SpeedTest>> speedTests = speedTestRepository.findSpeedTestsByUserIds(userIds, pageable)
                    .stream()
                    .collect(Collectors.groupingBy(speedTest -> speedTest.getUser().getId()));
            List<ActiveSessionsResponse> activeSessionsResponses = new ArrayList<>();
            speedTests.forEach((sellerId, speedTestList) -> {
                List<Long> speedTestIds = speedTestList.stream()
                        .map(SpeedTest::getId)
                        .collect(Collectors.toList());
                Map<User, List<Session>> sessions = sessionRepository.findActiveSessionsBySpeedTestIds(speedTestIds)
                        .stream()
                        .max(Comparator.comparing(Session::getId))
                        .stream().collect(Collectors.groupingBy(session -> session.getSpeedTest().getUser()));
                sessions.forEach((seller, sessionList) -> {
                    //sessionList will have only 1 object as it has been filtered
                    double availableData = sessionList.get(0).getData() - sessionList.get(0).getDataUsed();
                    double availableDataPrice = (sessionList.get(0).getPrice() / sessionList.get(0).getData()) * availableData;
                    double downloadSpeed = sessionList.get(0).getSpeedTest().getDownloadSpeed();
                    double uploadSpeed = sessionList.get(0).getSpeedTest().getUploadSpeed();
                    ActiveSessionsResponse activeSessionsResponse = new ActiveSessionsResponse();
                    activeSessionsResponse.setUsername(seller.getUsername());
                    activeSessionsResponse.setUserPhotoUrl(seller.getPhotoUrl());
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
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Session> getSortedSessionsByStartTime(Long userId, int pageNumber, int elements, boolean isDescending) {
        try {
            List<SpeedTest> speedTests = speedTestService.getSortedTestByDateTime(userId, 0, Integer.MAX_VALUE, isDescending);
            List<Long> speedTestIds = speedTests
                    .stream()
                    .map(SpeedTest::getId)
                    .collect(Collectors.toList());

            Pageable sortedPageableByStartTime
                    = isDescending ?
                    PageRequest.of(pageNumber, elements, Sort.by("start_time").descending())
                    : PageRequest.of(pageNumber, elements, Sort.by("start_time"));

            return sessionRepository.findAllSessionsById(speedTestIds, sortedPageableByStartTime);
        } catch (Exception e) {
            log.error("Exception ", e);
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Session> getSortedSessionsByDataUsed(Long userId, int pageNumber, int elements, boolean isDescending) {
        try {
            List<SpeedTest> speedTests = speedTestService.getSortedTestByDateTime(userId, 0, Integer.MAX_VALUE, isDescending);
            List<Long> speedTestIds = speedTests
                    .stream()
                    .map(SpeedTest::getId)
                    .collect(Collectors.toList());

            Pageable sortedPageableByDataUsed
                    = isDescending ?
                    PageRequest.of(pageNumber, elements, Sort.by("data_used").descending())
                    : PageRequest.of(pageNumber, elements, Sort.by("data_used"));

            return sessionRepository.findAllSessionsById(speedTestIds, sortedPageableByDataUsed);
        } catch (Exception e) {
            log.error("Exception ", e);
        }
        return null;
    }

}
