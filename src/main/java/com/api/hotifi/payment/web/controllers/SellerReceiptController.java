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

    @GetMapping(path = "/get/date-time/{seller-payment-id}/{page-number}/{elements}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedSellerReceiptsByDateTime(@PathVariable(value = "seller-payment-id")
                                                               @Range(min = 1, message = "{seller.id.invalid}") Long sellerPaymentId,
                                                               @PathVariable(value = "page-number")
                                                               @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int pageNumber,
                                                               @PathVariable(value = "elements")
                                                               @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.elements.invalid}") int elements,
                                                               @PathVariable(value = "is-descending") boolean isDescending) {
        List<SellerReceiptResponse> sellerReceiptResponses = sellerReceiptService.getSortedSellerReceiptsByDateTime(sellerPaymentId, pageNumber, elements, isDescending);
        return new ResponseEntity<>(sellerReceiptResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/get/amount-paid/{seller-payment-id}/{page-number}/{elements}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedSellerReceiptsByAmountPaid(@PathVariable(value = "seller-payment-id")
                                                               @Range(min = 1, message = "{seller.id.invalid}") Long sellerPaymentId,
                                                               @PathVariable(value = "page-number")
                                                               @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int pageNumber,
                                                               @PathVariable(value = "elements")
                                                               @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.elements.invalid}") int elements,
                                                               @PathVariable(value = "is-descending") boolean isDescending) {
        List<SellerReceiptResponse> sellerReceiptResponses = sellerReceiptService.getSortedSellerReceiptsByAmountPaid(sellerPaymentId, pageNumber, elements, isDescending);
        return new ResponseEntity<>(sellerReceiptResponses, HttpStatus.OK);
    }

}
