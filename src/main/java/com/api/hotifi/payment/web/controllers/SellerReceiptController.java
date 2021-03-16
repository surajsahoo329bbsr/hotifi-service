package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.authorization.service.ICustomerAuthorizationService;
import com.api.hotifi.authorization.utils.AuthorizationUtils;
import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.payment.services.interfaces.ISellerReceiptService;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
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

import java.util.List;

@Validated
@RestController
@Api(tags = Constants.SELLER_RECEIPT_TAG)
@RequestMapping(path = "/seller-receipt")
public class SellerReceiptController {

    @Autowired
    private ISellerReceiptService sellerReceiptService;

    @Autowired
    private ICustomerAuthorizationService customerAuthorizationService;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Seller Receipt By Seller Receipt Id",
            notes = "Get Seller Receipt By Seller Receipt Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSellerReceipt(@PathVariable(value = "id") @Range(min = 1, message = "{id.invalid}") Long id) {
        SellerReceiptResponse sellerReceiptResponse =
                AuthorizationUtils.isAdministratorRole() && customerAuthorizationService.isAuthorizedBySellerReceiptId(id, AuthorizationUtils.getUserToken()) ?
                        sellerReceiptService.getSellerReceipt(id) : null;
        return new ResponseEntity<>(sellerReceiptResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/date-time/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Date-Time Seller Receipts By Seller Id",
            notes = "Get Sorted Date-Time Seller Receipts By Seller Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSortedSellerReceiptsByDateTime(@PathVariable(value = "seller-id")
                                                               @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                               @PathVariable(value = "page")
                                                               @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                               @PathVariable(value = "size")
                                                               @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                               @PathVariable(value = "is-descending") boolean isDescending) {
        List<SellerReceiptResponse> sellerReceiptResponses =
                AuthorizationUtils.isAdministratorRole() && customerAuthorizationService.isAuthorizedByUserId(sellerId, AuthorizationUtils.getUserToken()) ?
                        sellerReceiptService.getSortedSellerReceiptsByDateTime(sellerId, page, size, isDescending) : null;
        return new ResponseEntity<>(sellerReceiptResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/amount-paid/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Amount-Paid Seller Receipts By Seller Id",
            notes = "Get Sorted Amount-Paid Seller Receipts By Seller Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSortedSellerReceiptsByAmountPaid(@PathVariable(value = "seller-id")
                                                                 @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                                 @PathVariable(value = "page")
                                                                 @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                                 @PathVariable(value = "size")
                                                                 @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                                 @PathVariable(value = "is-descending") boolean isDescending) {
        List<SellerReceiptResponse> sellerReceiptResponses =
                AuthorizationUtils.isAdministratorRole() || customerAuthorizationService.isAuthorizedByUserId(sellerId, AuthorizationUtils.getUserToken()) ?
                        sellerReceiptService.getSortedSellerReceiptsByAmountPaid(sellerId, page, size, isDescending) : null;
        return new ResponseEntity<>(sellerReceiptResponses, HttpStatus.OK);
    }

}
