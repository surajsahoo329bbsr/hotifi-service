package com.api.hotifi.session.web.controller;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.model.Buyer;
import com.api.hotifi.session.service.ISessionService;
import com.api.hotifi.session.web.request.SessionRequest;
import com.api.hotifi.session.web.response.ActiveSessionsResponse;
import com.api.hotifi.session.web.response.AmountToBePaidResponse;
import com.api.hotifi.session.web.response.SessionSummaryResponse;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Validated
@RestController
@Api(tags = Constants.SESSION_TAG)
@RequestMapping(path = "/session")
public class SessionController {

    @Autowired
    private ISessionService sessionService;

    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Add Session Details",
            notes = "Add Session Details",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> addSession(@RequestBody @Validated SessionRequest sessionRequest) {
        Session session = sessionService.addSession(sessionRequest);
        return new ResponseEntity<>(session, HttpStatus.OK);
    }

    @GetMapping(path = "/active/{usernames}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Add Active Session Details",
            notes = "Add Active Session Details",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINSTRATOR')")
    public ResponseEntity<?> getActiveSessions(@PathVariable(value = "usernames") Set<String> usernames) {
        List<ActiveSessionsResponse> activeSessionsResponses = sessionService.getActiveSessions(usernames);
        return new ResponseEntity<>(activeSessionsResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/buyers/{session-id}/{is-active}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Buyer's List By Session Id",
            notes = "Get Buyer's List By Session Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINSTRATOR')")
    public ResponseEntity<?> getBuyers(@PathVariable(value = "session-id") @Range(min = 1, message = "{user.id.invalid}") Long sessionId, @PathVariable(value = "is-active") boolean isActive) {
        List<Buyer> getBuyers = sessionService.getBuyers(sessionId, isActive);
        return new ResponseEntity<>(getBuyers, HttpStatus.OK);
    }

    @PutMapping(path = "/buyers/notify/finish/{session-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Send Notifications To Current Buyers To Finish Wifi Session",
            notes = "Add Session Details",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> sendNotificationsToFinishSession(@PathVariable(value = "session-id") @Range(min = 1, message = "{user.id.invalid}") Long sessionId) {
        sessionService.sendNotificationsToFinishSession(sessionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/finish/{session-id}/{is-force-stop}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Finish Seller's Hotspot Session",
            notes = "Finish Seller's Hotspot Session",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> finishSession(@PathVariable(value = "session-id") @Range(min = 1, message = "{session.id.invalid}") Long sessionId, @PathVariable(value = "is-force-stop") boolean isForceStop) {
        sessionService.finishSession(sessionId, isForceStop);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/buyers/calculate/{session-id}/{data-to-be-used}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Calculate Payment For Data To Be Consumed For Buyer",
            notes = "Calculate Payment For Data To Be Consumed For Buyer",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINSTRATOR')")
    public ResponseEntity<?> calculatePaymentForDataToBeUsed(@PathVariable(value = "session-id") @Range(min = 1, message = "{session.id.invalid}") Long sessionId, @PathVariable(value = "data-to-be-used") @Range(min = 1, message = "{data.to.be.used.invalid}") int dataToBeUsed) {
        double amountToBePaid = sessionService.calculatePaymentForDataToBeUsed(sessionId, dataToBeUsed);
        return new ResponseEntity<>(new AmountToBePaidResponse(amountToBePaid), HttpStatus.OK);
    }

    @GetMapping(path = "/buyers/summary/{session-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Session Details By Session Id",
            notes = "Get Session Details By Session Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINSTRATOR')")
    public ResponseEntity<?> getSessionSummary(@PathVariable(value = "session-id") @Range(min = 1, message = "{session.id.invalid}") Long sessionId) {
        SessionSummaryResponse sessionSummaryResponse = sessionService.getSessionSummary(sessionId);
        return new ResponseEntity<>(sessionSummaryResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/start-time/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Start-Time Sessions By Seller Id",
            notes = "Get Sorted Start-Time Sessions By Seller Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSortedSessionsByStartTime(@PathVariable(value = "seller-id") @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                          @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                          @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                          @PathVariable(value = "is-descending") boolean isDescending) {
        List<SessionSummaryResponse> sessionSummaryResponses = sessionService.getSortedSessionsByStartTime(sellerId, page, size, isDescending);
        return new ResponseEntity<>(sessionSummaryResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/data-used/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Data-Used Sessions By Seller Id",
            notes = "Get Sorted Data-Used Sessions By Seller Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSortedSessionsByDataUsed(@PathVariable(value = "seller-id") @Range(min = 1, message = "{selelr.id.invalid}") Long sellerId,
                                                         @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                         @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                         @PathVariable(value = "is-descending") boolean isDescending) {
        List<SessionSummaryResponse> sessionSummaryResponses = sessionService.getSortedSessionsByDataUsed(sellerId, page, size, isDescending);
        return new ResponseEntity<>(sessionSummaryResponses, HttpStatus.OK);
    }
}
