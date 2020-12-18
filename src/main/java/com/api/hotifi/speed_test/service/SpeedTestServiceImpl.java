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
            SpeedTest speedTest = new SpeedTest();
            speedTest.setNetworkName(speedTestRequest.getNetworkName());
            speedTest.setUploadSpeed(speedTestRequest.getUploadSpeed());
            speedTest.setDownloadSpeed(speedTestRequest.getDownloadSpeed());
            speedTest.setPinCode(speedTestRequest.getPinCode());
            speedTest.setUser(user);
            speedTestRepository.save(speedTest);
        } catch (Exception e) {
            log.error("Error occurred", e);
        }
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
