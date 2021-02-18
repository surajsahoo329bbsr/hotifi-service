package com.api.hotifi.speedtest.service;

import com.api.hotifi.speedtest.entity.SpeedTest;
import com.api.hotifi.speedtest.web.request.SpeedTestRequest;

import java.util.List;

public interface ISpeedTestService {

    void addSpeedTest(SpeedTestRequest speedTestRequest);

    SpeedTest getLatestSpeedTest(Long userId, String pinCode, boolean isWifi);

    List<SpeedTest> getSortedSpeedTestByUploadSpeed(Long userId, int page, int size, boolean isDescending);

    List<SpeedTest> getSortedTestByDownloadSpeed(Long userId, int page, int size, boolean isDescending);

    List<SpeedTest> getSortedSpeedTestByDateTime(Long userId, int page, int size, boolean isDescending);

}
