package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.authorization.service.ICustomerAutorizationService;
import com.api.hotifi.authorization.utils.AuthorizationUtils;
import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.payment.services.interfaces.IStatsService;
import com.api.hotifi.payment.web.responses.BuyerStatsResponse;
import com.api.hotifi.payment.web.responses.SellerStatsResponse;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Api(tags = Constants.STATS_TAG)
@RequestMapping(path = "/stats")
public class StatsController {

    @Autowired
    private IStatsService statsService;

    @Autowired
    private ICustomerAutorizationService customerAutorizationService;

    @GetMapping(path = "/seller/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Seller Stats By User Id",
            notes = "Get Seller Stats By User Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTARTOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSellerStats(@PathVariable("id") @Range(min = 1, message = "{seller.id.invalid}") Long id) {
        SellerStatsResponse sellerStatsResponse =
                AuthorizationUtils.isAdminstratorRole() || customerAutorizationService.isAuthorizedByUserId(id, AuthorizationUtils.getUserToken()) ?
                        statsService.getSellerStats(id) : null;
        return new ResponseEntity<>(sellerStatsResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/buyer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Buyer Stats By User Id",
            notes = "Get Buyer Stats By User Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTARTOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getBuyerStats(@PathVariable("id") @Range(min = 1, message = "{buyer.id.invalid}") Long id) {
        BuyerStatsResponse buyerStatsResponse =
                AuthorizationUtils.isAdminstratorRole() || customerAutorizationService.isAuthorizedByUserId(id, AuthorizationUtils.getUserToken()) ?
                        statsService.getBuyerStats(id) : null;
        return new ResponseEntity<>(buyerStatsResponse, HttpStatus.OK);
    }

}
