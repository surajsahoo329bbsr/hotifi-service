package com.api.hotifi.offer.web.controllers;

import com.api.hotifi.common.constants.configurations.AppConfigurations;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Api(tags = AppConfigurations.OFFER_TAG)
@RequestMapping(path = "/offer")
public class OfferController {
}
