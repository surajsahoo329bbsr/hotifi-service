package com.api.hotifi.speed_test.service;

import com.api.hotifi.speed_test.entity.SpeedTest;
import com.api.hotifi.speed_test.web.request.SpeedTestRequest;

import java.util.List;

public interface ISpeedTestService {

    void addSpeedTest(SpeedTestRequest speedTestRequest);

    SpeedTest getLatestSpeedTest(Long userId, String pinCode, boolean isWifi);

    List<SpeedTest> sortSpeedTestByUploadSpeed(Long userId, int pageNumber, int elements, boolean isDescending);

    List<SpeedTest> sortSpeedTestByDownloadSpeed(Long userId, int pageNumber, int elements, boolean isDescending);

    List<SpeedTest> sortSpeedTestByDateTime(Long userId, int pageNumber, int elements, boolean isDescending);

}
