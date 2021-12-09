package com.api.hotifi.offer.web.controllers;

import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.offer.services.interfaces.IReferrerService;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Api(tags = AppConfigurations.REFERRER_TAG)
@RequestMapping(path = "/referrer")
public class ReferrerController {

    @Autowired
    private IReferrerService referrerService;

    @GetMapping(path = "/{user-id}")
    public ResponseEntity<?> isValidReferralUserId(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId) {
        boolean isValid = referrerService.isValidReferralUserId(userId);
        return new ResponseEntity<>(isValid, HttpStatus.OK);
    }
}
