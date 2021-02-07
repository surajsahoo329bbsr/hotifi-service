package com.api.hotifi.speed_test.web.controller;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.speed_test.entity.SpeedTest;
import com.api.hotifi.speed_test.service.ISpeedTestService;
import com.api.hotifi.speed_test.web.request.SpeedTestRequest;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Finish Seller's Hotspot Session",
            notes = "Finish Seller's Hotspot Session",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> addSpeedTest(@RequestBody @Validated SpeedTestRequest speedTestRequest) {
        speedTestService.addSpeedTest(speedTestRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/{user-id}/{pin-code}/{is-wifi}")
    @ApiOperation(
            value = "Finish Seller's Hotspot Session",
            notes = "Finish Seller's Hotspot Session",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getLatestSpeedTest(@PathVariable(value = "user-id")
                                                @Range(min = 1, message = "{user.id.invalid}") Long userId,
                                                @NotBlank(message = "{pin.code.blank}")
                                                @PathVariable(value = "pin-code") String pinCode,
                                                @PathVariable(value = "is-wifi") boolean isWifi) {
        SpeedTest speedTest = speedTestService.getLatestSpeedTest(userId, pinCode, isWifi);
        return new ResponseEntity<>(speedTest, HttpStatus.OK);
    }

    @GetMapping(path = "/date-time/{user-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Date-Time Speedtests By User Id And Pagenation Values",
            notes = "Get Sorted Date-Time Speedtests By User Id And Pagenation Values",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getSortedSpeedTestByDateTime(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId,
                                                          @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                          @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                          @PathVariable(value = "is-descending") boolean isDescending) {
        List<SpeedTest> speedTests = speedTestService.getSortedSpeedTestByDateTime(userId, page, size, isDescending);
        return new ResponseEntity<>(speedTests, HttpStatus.OK);
    }

    @GetMapping(path = "/upload-speed/{user-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Upload-Speed Speedtests By User Id And Pagenation Values",
            notes = "Get Sorted Upload-Speed Speedtests By User Id And Pagenation Values",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getSortedSpeedTestByUploadSpeed(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId,
                                                             @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                             @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                             @PathVariable(value = "is-descending") boolean isDescending) {
        List<SpeedTest> speedTests = speedTestService.getSortedSpeedTestByUploadSpeed(userId, page, size, isDescending);
        return new ResponseEntity<>(speedTests, HttpStatus.OK);
    }

    @GetMapping(path = "/download-speed/{user-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Download-Speed Speedtests By User Id And Pagenation Values",
            notes = "Get Sorted Download-Speed Speedtests By User Id And Pagenation Values",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getSortedSpeedTestByDownloadSpeed(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId,
                                                               @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                               @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                               @PathVariable(value = "is-descending") boolean isDescending) {
        List<SpeedTest> speedTests = speedTestService.getSortedTestByDownloadSpeed(userId, page, size, isDescending);
        return new ResponseEntity<>(speedTests, HttpStatus.OK);
    }
}
