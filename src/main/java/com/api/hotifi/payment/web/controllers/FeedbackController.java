package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.payment.entities.Feedback;
import com.api.hotifi.payment.services.interfaces.IFeedbackService;
import com.api.hotifi.payment.web.request.FeedbackRequest;
import com.api.hotifi.payment.web.responses.FeedbackResponse;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Api(tags = Constants.FEEDBACK_TAG)
@RequestMapping(path = "/feedback")
public class FeedbackController {

    @Autowired
    private IFeedbackService feedbackService;

    @PostMapping(path = "/buyer", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Add Feedback Of A Purchase By A Buyer",
            notes = "Add Feedback Of A Purchase By A Buyer",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> addFeedback(@RequestBody @Validated FeedbackRequest feedbackRequest) {
        feedbackService.addFeedback(feedbackRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/buyer/feedback/{purchase-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Feedback Details By Purchase Id",
            notes = "Get Feedback Details By Purchase Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getPurchaseFeedback(@PathVariable(value = "purchase-id") @Range(min = 1, message = "{purchase.id.invalid}") Long purchaseId) {
        Feedback feedback = feedbackService.getPurchaseFeedback(purchaseId);
        return new ResponseEntity<>(feedback, HttpStatus.OK);
    }

    @GetMapping(path = "/seller/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Seller Feeback By Seller Id And Pagenation Values",
            notes = "Get Seller Feeback By Seller Id And Pagenation Values",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSellerFeedbacks(@PathVariable(value = "seller-id")
                                                @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                @PathVariable(value = "page")
                                                @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                @PathVariable(value = "size")
                                                @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                @PathVariable(value = "is-descending") boolean isDescending){
        List<FeedbackResponse> feedbackResponses = feedbackService.getSellerFeedbacks(sellerId, page, size, isDescending);
        return new ResponseEntity<>(feedbackResponses, HttpStatus.OK);
    }
}
