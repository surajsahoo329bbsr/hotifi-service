package com.api.hotifi.speed_test.service;

import com.api.hotifi.speed_test.entity.SpeedTest;
import com.api.hotifi.speed_test.web.request.SpeedTestRequest;

import java.util.List;

public interface ISpeedTestService {

    void addSpeedTest(SpeedTestRequest speedTestRequest);

    List<SpeedTest> sortSpeedTestByUploadSpeed(Long userId, int pageNumber, int elements, boolean isDescending);

    List<SpeedTest> sortSpeedTestByDownloadSpeed(Long userId, int pageNumber, int elements, boolean isDescending);

    List<SpeedTest> sortSpeedTestByDateTime(Long userId, int pageNumber, int elements, boolean isDescending);

}
