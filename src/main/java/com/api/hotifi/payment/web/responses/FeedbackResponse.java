package com.api.hotifi.payment.web.responses;

import com.api.hotifi.payment.entities.Feedback;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackResponse {

    private Feedback feedback;

    private String buyerPhotoUrl;

    private String buyerName;
}
