package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.common.constant.Constants;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Api(tags = Constants.SELLER_RECEIPT_TAG)
@RequestMapping(path = "/seller-receipt")
public class SellerReceiptController {
}
