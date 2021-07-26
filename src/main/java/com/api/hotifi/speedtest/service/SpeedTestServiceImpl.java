package com.api.hotifi.speedtest.service;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.speedtest.codes.NetworkProviderCodes;
import com.api.hotifi.speedtest.entity.SpeedTest;
import com.api.hotifi.speedtest.error.SpeedTestErrorCodes;
import com.api.hotifi.speedtest.repository.SpeedTestRepository;
import com.api.hotifi.speedtest.web.request.SpeedTestRequest;
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

        boolean isBelowWifiDownloadSpeed = Double.compare(speedTestRequest.getDownloadSpeed(), BusinessConfigurations.MINIMUM_WIFI_DOWNLOAD_SPEED_MEGABYTES) < 0
                && speedTestRequest.getNetworkProvider().equals(NetworkProviderCodes.WIFI.name());

        boolean isBelowWifiUploadSpeed = Double.compare(speedTestRequest.getUploadSpeed(), BusinessConfigurations.MINIMUM_WIFI_UPLOAD_SPEED_MEGABYTES) < 0
                && speedTestRequest.getNetworkProvider().equals(NetworkProviderCodes.WIFI.name());

        if(isBelowWifiDownloadSpeed || isBelowWifiUploadSpeed){
            throw new HotifiException(SpeedTestErrorCodes.SPEED_TEST_INVALID_WIFI_RECORD);
        }

        User user = userRepository.findById(speedTestRequest.getUserId()).orElse(null);
        if (!LegitUtils.isUserLegit(user) && !(user != null && user.isLoggedIn()))
            throw new HotifiException(UserErrorCodes.USER_NOT_LEGIT);
        try {
            SpeedTest speedTest = new SpeedTest();
            speedTest.setNetworkProvider(speedTestRequest.getNetworkProvider());
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
            throw new HotifiException(SpeedTestErrorCodes.SPEED_TEST_RECORD_NOT_FOUND);
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
