package com.api.hotifi.session.service;

import com.api.hotifi.common.constant.AES;
import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.repository.UserRepository;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.repository.SessionRepository;
import com.api.hotifi.session.web.request.SessionRequest;
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

import java.util.List;
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

    @Transactional
    @Override
    public void addSession(SessionRequest sessionRequest) {
        try {
            //Do not check if user is banned or freezed, because that checking is for buyers only
            User user = userRepository.getOne(sessionRequest.getUserId());
            if (!LegitUtils.isUserLegit(user) && user.isLoggedIn())
                throw new Exception("User is not activated or deleted or not logged in");

            SpeedTest speedTest = speedTestService.getLatestSpeedTest(sessionRequest.getUserId(), sessionRequest.getPinCode(), sessionRequest.isWifi());
            if (speedTest == null && sessionRequest.isWifi())
                throw new Exception("User has not done wifi speed test");
            if (speedTest == null && !sessionRequest.isWifi())
                throw new Exception("User has not done speed test");

            //Everything is fine, so encrypt the password
            String encryptedString = AES.encrypt(sessionRequest.getWifiPassword(), Constants.WIFI_PASSWORD_SECRET_KEY);

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

    @Override
    public List<Session> getActiveSessions(List<String> username, int pageNumber, int elements) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Session> sortAllSessionsByStartTime(Long userId, int pageNumber, int elements, boolean isDescending) {
        try {
            List<SpeedTest> speedTests = speedTestService.sortSpeedTestByDateTime(userId, 0, Integer.MAX_VALUE, isDescending);
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
    public List<Session> sortAllSessionsByDataUsed(Long userId, int pageNumber, int elements, boolean isDescending) {
        try {
            List<SpeedTest> speedTests = speedTestService.sortSpeedTestByDateTime(userId, 0, Integer.MAX_VALUE, isDescending);
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
