package com.api.hotifi.session.web.controller;

import com.api.hotifi.authorization.service.ICustomerAuthorizationService;
import com.api.hotifi.authorization.utils.AuthorizationUtils;
import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.common.constants.messages.SuccessMessages;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Validated
@RestController
@Api(tags = AppConfigurations.SESSION_TAG)
@RequestMapping(path = "/session")
public class SessionController {

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private ICustomerAuthorizationService customerAuthorizationService;

    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Add Session Details",
            notes = "Add Session Details",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = Session.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> addSession(@RequestBody @Validated SessionRequest sessionRequest) {
        Session session = customerAuthorizationService.isAuthorizedByUserId(sessionRequest.getUserId(), AuthorizationUtils.getUserToken()) ?
                sessionService.addSession(sessionRequest) : null;
        return new ResponseEntity<>(session, HttpStatus.OK);
    }

    @GetMapping(path = "/active/{usernames}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Active Session Details",
            notes = "Get Active Session Details",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = ActiveSessionsResponse.class, responseContainer = "List")
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINISTRATOR')")
    //Any customer / admin can view this no need to check for particular customer
    public ResponseEntity<?> getActiveSessions(@RequestParam(value = "usernames") Set<String> usernames) {
        //Not required to check for role security
        List<ActiveSessionsResponse> activeSessionsResponses = sessionService.getActiveSessions(usernames);
        return new ResponseEntity<>(activeSessionsResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/buyers/{session-id}/{is-active}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Buyer's List By Session Id",
            notes = "Get Buyer's List By Session Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = Buyer.class, responseContainer = "List")
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getBuyers(@PathVariable(value = "session-id")
                                       @Range(min = 1, message = "{user.id.invalid}") Long sessionId,
                                       @PathVariable(value = "is-active") boolean isActive) {
        List<Buyer> getBuyers =
                AuthorizationUtils.isAdministratorRole() || customerAuthorizationService.isAuthorizedBySessionId(sessionId, AuthorizationUtils.getUserToken()) ?
                sessionService.getBuyers(sessionId, isActive) : null;
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
    public ResponseEntity<?> sendNotificationsToFinishSession(@PathVariable(value = "session-id")
                                                              @Range(min = 1, message = "{user.id.invalid}") Long sessionId) {
        if (customerAuthorizationService.isAuthorizedBySessionId(sessionId, AuthorizationUtils.getUserToken()))
            sessionService.sendNotificationsToFinishSession(sessionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/finish/{session-id}/{is-force-stop}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Finish Seller's Hotspot Session",
            notes = "Finish Seller's Hotspot Session",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> finishSession(@PathVariable(value = "session-id")
                                           @Range(min = 1, message = "{session.id.invalid}") Long sessionId,
                                           @PathVariable(value = "is-force-stop") boolean isForceStop) {
        if (customerAuthorizationService.isAuthorizedBySessionId(sessionId, AuthorizationUtils.getUserToken()))
            sessionService.finishSession(sessionId, isForceStop);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/buyers/calculate/{session-id}/{data-to-be-used}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Calculate Payment For Data To Be Consumed For Buyer",
            notes = "Calculate Payment For Data To Be Consumed For Buyer",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = AmountToBePaidResponse.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> calculatePaymentForDataToBeUsed(@PathVariable(value = "session-id")
                                                             @Range(min = 1, message = "{session.id.invalid}") Long sessionId,
                                                             @PathVariable(value = "data-to-be-used")
                                                             @Range(min = 1, message = "{data.to.be.used.invalid}") int dataToBeUsed) {
        //No need to check for role security
        BigDecimal amountToBePaid = sessionService.calculatePaymentForDataToBeUsed(sessionId, dataToBeUsed);
        return new ResponseEntity<>(new AmountToBePaidResponse(amountToBePaid), HttpStatus.OK);
    }

    @GetMapping(path = "/buyers/summary/{session-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Session Details By Session Id",
            notes = "Get Session Details By Session Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = SessionSummaryResponse.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getSessionSummary(@PathVariable(value = "session-id")
                                               @Range(min = 1, message = "{session.id.invalid}") Long sessionId) {
        SessionSummaryResponse sessionSummaryResponse =
                AuthorizationUtils.isAdministratorRole() || customerAuthorizationService.isAuthorizedBySessionId(sessionId, AuthorizationUtils.getUserToken()) ?
                        sessionService.getSessionSummary(sessionId) : null;
        return new ResponseEntity<>(sessionSummaryResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/seller/date-time/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Start-Time Sessions By Seller Id",
            notes = "Get Sorted Start-Time Sessions By Seller Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = SessionSummaryResponse.class, responseContainer = "List")
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSortedSessionsByStartTime(@PathVariable(value = "seller-id") @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                          @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                          @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                          @PathVariable(value = "is-descending") boolean isDescending) {
        List<SessionSummaryResponse> sessionSummaryResponses =
                customerAuthorizationService.isAuthorizedByUserId(sellerId, AuthorizationUtils.getUserToken()) ?
                        sessionService.getSortedSessionsByDateTime(sellerId, page, size, isDescending) : null;
        return new ResponseEntity<>(sessionSummaryResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/seller/data-shared/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Data-Shared Sessions By Seller Id",
            notes = "Get Sorted Data-Shared Sessions By Seller Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = SessionSummaryResponse.class, responseContainer = "List")
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSortedSessionsByDataShared(@PathVariable(value = "seller-id") @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                           @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                           @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                           @PathVariable(value = "is-descending") boolean isDescending) {
        List<SessionSummaryResponse> sessionSummaryResponses =
                customerAuthorizationService.isAuthorizedByUserId(sellerId, AuthorizationUtils.getUserToken()) ?
                        sessionService.getSortedSessionsByDataShared(sellerId, page, size, isDescending) : null;
        return new ResponseEntity<>(sessionSummaryResponses, HttpStatus.OK);
    }
}
