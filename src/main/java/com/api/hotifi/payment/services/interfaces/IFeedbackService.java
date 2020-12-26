package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.payment.entities.Feedback;
import com.api.hotifi.payment.web.request.FeedbackRequest;
import com.api.hotifi.payment.web.responses.FeedbackResponse;

import java.util.List;

public interface IFeedbackService {

    void addFeedback(FeedbackRequest feedbackRequest);

    Feedback getPurchaseFeedback(Long purchaseId);

    List<FeedbackResponse> getSellerFeedbacks(Long sellerId, int pageNumber, int elements, boolean isDescending);
}
