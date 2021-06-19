package com.api.hotifi.payment.web.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SellerReviewsResponse {

    private final long totalReviews;

    private final long totalRatings;

    private final String averageRating;

    private final List<Long> eachRatings;

}
