package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.services.interfaces.IStatsService;
import com.api.hotifi.payment.web.responses.BuyerStatsResponse;
import com.api.hotifi.payment.web.responses.SellerStatsResponse;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(path = "/seller/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSellerStats(@PathVariable("id") @Range(min = 1, message = "{seller.id.invalid}") Long id){
        SellerStatsResponse sellerStatsResponse = statsService.getSellerStats(id);
        return new ResponseEntity<>(sellerStatsResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/buyer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBuyerStats(@PathVariable("id") @Range(min = 1, message = "{buyer.id.invalid}") Long id){
        BuyerStatsResponse buyerStatsResponse = statsService.getBuyerStats(id);
        return new ResponseEntity<>(buyerStatsResponse, HttpStatus.OK);
    }

}
