package com.api.hotifi.speed_test.service;

import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.repository.UserRepository;
import com.api.hotifi.speed_test.entity.SpeedTest;
import com.api.hotifi.speed_test.repository.SpeedTestRepository;
import com.api.hotifi.speed_test.web.request.SpeedTestRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SpeedTestServiceImpl implements ISpeedTestService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SpeedTestRepository speedTestRepository;

    @Transactional
    @Override
    public void addSpeedTest(SpeedTestRequest speedTestRequest) {
        try {
            User user = userRepository.getOne(speedTestRequest.getUserId());
            if(user.getAuthentication().isDeleted())
                throw new Exception("User is deleted");
            SpeedTest speedTest = new SpeedTest();
            speedTest.setNetworkName(speedTestRequest.getNetworkName());
            speedTest.setDownloadSpeed(speedTestRequest.getDownloadSpeed());
            speedTest.setUploadSpeed(speedTestRequest.getUploadSpeed());
            speedTest.setPinCode(speedTestRequest.getPinCode());
            speedTest.setUser(user);
            speedTestRepository.save(speedTest);
        } catch (Exception e) {
            log.error("Error occurred", e);
        }
    }

    @Transactional
    @Override
    public SpeedTest getLatestSpeedTest(Long userId, String pinCode, boolean isWifi) {
        try {
            SpeedTest speedTest = isWifi ?
                    speedTestRepository.findLatestWifiSpeedTest(userId, pinCode) :
                    speedTestRepository.findLatestNonWifiSpeedTest(userId, pinCode);
            if(speedTest == null)
                throw new Exception("No Speed Test Record Exists");
            return speedTest;
        } catch (Exception e) {
            log.error("Error occurred", e);
        }
        return null;
    }

    //For Get Speed Tests call sortByDateTime in Descending format
    @Transactional
    @Override
    public List<SpeedTest> sortSpeedTestByDateTime(Long userId, int pageNumber, int elements, boolean isDescending) {
        try {
            Pageable sortedPageableByDateTime
                    = isDescending ?
                    PageRequest.of(pageNumber, elements, Sort.by("created_at").descending())
                    : PageRequest.of(pageNumber, elements, Sort.by("created_at"));
            return speedTestRepository.findSpeedTestsByUserId(userId, sortedPageableByDateTime);
        } catch (Exception e) {
            log.error("Exception occurred : " + e);
        }
        return null;
    }

    @Transactional
    @Override
    public List<SpeedTest> sortSpeedTestByUploadSpeed(Long userId, int pageNumber, int elements, boolean isDescending) {
        try {
            Pageable sortedPageableByUploadSpeed
                    = isDescending ?
                    PageRequest.of(pageNumber, elements, Sort.by("upload_speed").descending())
                    : PageRequest.of(pageNumber, elements, Sort.by("upload_speed"));
            return speedTestRepository.findSpeedTestsByUserId(userId, sortedPageableByUploadSpeed);
        } catch (Exception e) {
            log.error("Exception occurred : " + e);
        }
        return null;
    }

    @Transactional
    @Override
    public List<SpeedTest> sortSpeedTestByDownloadSpeed(Long userId, int pageNumber, int elements, boolean isDescending) {
        try {
            Pageable sortedPageableByDownloadSpeed
                    = isDescending ?
                    PageRequest.of(pageNumber, elements, Sort.by("download_speed").descending())
                    : PageRequest.of(pageNumber, elements, Sort.by("download_speed"));
            return speedTestRepository.findSpeedTestsByUserId(userId, sortedPageableByDownloadSpeed);
        } catch (Exception e) {
            log.error("Exception occurred : " + e);
        }
        return null;
    }

}
