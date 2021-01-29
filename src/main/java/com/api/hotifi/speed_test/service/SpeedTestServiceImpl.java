package com.api.hotifi.speed_test.service;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.speed_test.entity.SpeedTest;
import com.api.hotifi.speed_test.error.SpeedTestErrorCodes;
import com.api.hotifi.speed_test.repository.SpeedTestRepository;
import com.api.hotifi.speed_test.web.request.SpeedTestRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
public class SpeedTestServiceImpl implements ISpeedTestService {

    private final UserRepository userRepository;
    private final SpeedTestRepository speedTestRepository;

    public SpeedTestServiceImpl(UserRepository userRepository, SpeedTestRepository speedTestRepository) {
        this.userRepository = userRepository;
        this.speedTestRepository = speedTestRepository;
    }

    @Transactional
    @Override
    public void addSpeedTest(SpeedTestRequest speedTestRequest) {
        User user = userRepository.findById(speedTestRequest.getUserId()).orElse(null);
        if (!LegitUtils.isUserLegit(user) && !(user != null && user.isLoggedIn()))
            throw new HotifiException(UserErrorCodes.USER_NOT_LEGIT);
        try {
            SpeedTest speedTest = new SpeedTest();
            speedTest.setNetworkName(speedTestRequest.getNetworkName());
            speedTest.setDownloadSpeed(speedTestRequest.getDownloadSpeed());
            speedTest.setUploadSpeed(speedTestRequest.getUploadSpeed());
            speedTest.setPinCode(speedTestRequest.getPinCode());
            speedTest.setUser(user);
            speedTestRepository.save(speedTest);
        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new HotifiException(SpeedTestErrorCodes.UNEXPECTED_SPEED_TEST_ERROR);
        }
    }

    @Transactional
    @Override
    public SpeedTest getLatestSpeedTest(Long userId, String pinCode, boolean isWifi) {
        SpeedTest speedTest = isWifi ?
                speedTestRepository.findLatestWifiSpeedTest(userId, pinCode) :
                speedTestRepository.findLatestNonWifiSpeedTest(userId, pinCode);
        if (speedTest == null)
            throw new HotifiException(SpeedTestErrorCodes.NO_SPEED_TEST_RECORD_EXISTS);
        return speedTest;
    }

    //For Get Speed Tests call sortByDateTime in Descending format
    @Transactional
    @Override
    public List<SpeedTest> getSortedSpeedTestByDateTime(Long userId, int page, int size, boolean isDescending) {
        try {
            Pageable sortedPageableByDateTime
                    = isDescending ?
                    PageRequest.of(page, size, Sort.by("created_at").descending())
                    : PageRequest.of(page, size, Sort.by("created_at"));
            return speedTestRepository.findSpeedTestsByUserId(userId, sortedPageableByDateTime);
        } catch (Exception e) {
            log.error("Exception occurred : " + e);
            throw new HotifiException(SpeedTestErrorCodes.UNEXPECTED_SPEED_TEST_ERROR);
        }
    }

    @Transactional
    @Override
    public List<SpeedTest> getSortedSpeedTestByUploadSpeed(Long userId, int page, int size, boolean isDescending) {
        try {
            Pageable sortedPageableByUploadSpeed
                    = isDescending ?
                    PageRequest.of(page, size, Sort.by("upload_speed").descending())
                    : PageRequest.of(page, size, Sort.by("upload_speed"));
            return speedTestRepository.findSpeedTestsByUserId(userId, sortedPageableByUploadSpeed);
        } catch (Exception e) {
            log.error("Exception occurred : " + e);
            throw new HotifiException(SpeedTestErrorCodes.UNEXPECTED_SPEED_TEST_ERROR);
        }
    }

    @Transactional
    @Override
    public List<SpeedTest> getSortedTestByDownloadSpeed(Long userId, int page, int size, boolean isDescending) {
        try {
            Pageable sortedPageableByDownloadSpeed
                    = isDescending ?
                    PageRequest.of(page, size, Sort.by("download_speed").descending())
                    : PageRequest.of(page, size, Sort.by("download_speed"));
            return speedTestRepository.findSpeedTestsByUserId(userId, sortedPageableByDownloadSpeed);
        } catch (Exception e) {
            log.error("Exception occurred : " + e);
            throw new HotifiException(SpeedTestErrorCodes.UNEXPECTED_SPEED_TEST_ERROR);
        }
    }

}
