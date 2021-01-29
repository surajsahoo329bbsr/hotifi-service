package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.payment.services.interfaces.ISellerPaymentService;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Api(tags = Constants.SELLER_PAYMENT_TAG)
@RequestMapping(path = "/seller-payment")
public class SellerPaymentController {

    @Autowired
    private ISellerPaymentService sellerPaymentService;

    @PutMapping(path = "/withdraw/{seller-id}")
    @ApiOperation(
            value = "Get Bank Account Details By User Id",
            notes = "Get Bank Account Details By User Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINSTRATOR')")
    public ResponseEntity<?> withdrawSellerPayment(@PathVariable(value = "seller-id") @Range(min = 1, message = "{seller.id.invalid}") Long sellerId) {
        SellerReceiptResponse sellerReceiptResponse = sellerPaymentService.withdrawSellerPayment(sellerId);
        return new ResponseEntity<>(sellerReceiptResponse, HttpStatus.OK);
    }

}
