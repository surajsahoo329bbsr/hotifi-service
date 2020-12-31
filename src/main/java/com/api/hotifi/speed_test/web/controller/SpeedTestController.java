package com.api.hotifi.speed_test.web.controller;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.speed_test.entity.SpeedTest;
import com.api.hotifi.speed_test.service.ISpeedTestService;
import com.api.hotifi.speed_test.web.request.SpeedTestRequest;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@RestController
@Api(tags = Constants.SPEED_TEST_TAG)
@RequestMapping(path = "/speed-test")
public class SpeedTestController {

    @Autowired
    private ISpeedTestService speedTestService;

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSpeedTest(@RequestBody @Validated SpeedTestRequest speedTestRequest) {
        speedTestService.addSpeedTest(speedTestRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/get/{user-id}/{pin-code}/{is-wifi}")
    public ResponseEntity<?> getLatestSpeedTest(@PathVariable(value = "user-id")
                                                @Range(min = 1, message = "{user.id.invalid}") Long userId,
                                                @NotBlank(message = "{pin.code.blank}")
                                                @PathVariable(value = "pin-code") String pinCode,
                                                @PathVariable(value = "is-wifi") boolean isWifi) {
        SpeedTest speedTest = speedTestService.getLatestSpeedTest(userId, pinCode, isWifi);
        return new ResponseEntity<>(speedTest, HttpStatus.OK);
    }

    @GetMapping(path = "/get/date-time/{user-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedSpeedTestByDateTime(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId,
                                                          @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                          @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                          @PathVariable(value = "is-descending") boolean isDescending) {
        List<SpeedTest> speedTests = speedTestService.getSortedTestByDateTime(userId, page, size, isDescending);
        return new ResponseEntity<>(speedTests, HttpStatus.OK);
    }

    @GetMapping(path = "/get/upload-speed/{user-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedSpeedTestByUploadSpeed(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId,
                                                             @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                             @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                             @PathVariable(value = "is-descending") boolean isDescending) {
        List<SpeedTest> speedTests = speedTestService.getSortedSpeedTestByUploadSpeed(userId, page, size, isDescending);
        return new ResponseEntity<>(speedTests, HttpStatus.OK);
    }

    @GetMapping(path = "/get/download-speed/{user-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedSpeedTestByDownloadSpeed(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId,
                                                               @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                               @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                               @PathVariable(value = "is-descending") boolean isDescending) {
        List<SpeedTest> speedTests = speedTestService.getSortedTestByDownloadSpeed(userId, page, size, isDescending);
        return new ResponseEntity<>(speedTests, HttpStatus.OK);
    }
}
