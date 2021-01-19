package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.services.interfaces.ISellerReceiptService;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
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

import java.util.List;

@Validated
@RestController
@Api(tags = Constants.SELLER_RECEIPT_TAG)
@RequestMapping(path = "/seller-receipt")
public class SellerReceiptController {

    @Autowired
    private ISellerReceiptService sellerReceiptService;

    @GetMapping(path = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSellerReceipt(@PathVariable(value = "id") @Range(min = 1, message = "{id.invalid}") Long id) {
        SellerReceiptResponse sellerReceiptResponse = sellerReceiptService.getSellerReceipt(id);
        return new ResponseEntity<>(sellerReceiptResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/get/date-time/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedSellerReceiptsByDateTime(@PathVariable(value = "seller-id")
                                                               @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                               @PathVariable(value = "page")
                                                               @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                               @PathVariable(value = "size")
                                                               @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                               @PathVariable(value = "is-descending") boolean isDescending) {
        List<SellerReceiptResponse> sellerReceiptResponses = sellerReceiptService.getSortedSellerReceiptsByDateTime(sellerId, page, size, isDescending);
        return new ResponseEntity<>(sellerReceiptResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/get/amount-paid/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedSellerReceiptsByAmountPaid(@PathVariable(value = "seller-id")
                                                               @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                               @PathVariable(value = "page")
                                                               @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                               @PathVariable(value = "size")
                                                               @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                               @PathVariable(value = "is-descending") boolean isDescending) {
        List<SellerReceiptResponse> sellerReceiptResponses = sellerReceiptService.getSortedSellerReceiptsByAmountPaid(sellerId, page, size, isDescending);
        return new ResponseEntity<>(sellerReceiptResponses, HttpStatus.OK);
    }

}
