package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.entities.Feedback;
import com.api.hotifi.payment.services.interfaces.IFeedbackService;
import com.api.hotifi.payment.web.request.FeedbackRequest;
import com.api.hotifi.payment.web.responses.FeedbackResponse;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addFeedback(@RequestBody @Validated FeedbackRequest feedbackRequest) {
        feedbackService.addFeedback(feedbackRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/get/purchase/feedback/{purchase-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPurchaseFeedback(@PathVariable(value = "purchase-id") @Range(min = 1, message = "{purchase.id.invalid}") Long purchaseId) {
        Feedback feedback = feedbackService.getPurchaseFeedback(purchaseId);
        return new ResponseEntity<>(feedback, HttpStatus.OK);
    }

    @GetMapping(path = "/get/seller/feedback/{seller-id}/{page-number}/{elements}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSellerFeedbacks(@PathVariable(value = "seller-id")
                                                @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                @PathVariable(value = "page-number")
                                                @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int pageNumber,
                                                @PathVariable(value = "elements")
                                                @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.elements.invalid}") int elements,
                                                @PathVariable(value = "is-descending") boolean isDescending){
        List<FeedbackResponse> feedbackResponses = feedbackService.getSellerFeedbacks(sellerId, pageNumber, elements, isDescending);
        return new ResponseEntity<>(feedbackResponses, HttpStatus.OK);
    }
}
