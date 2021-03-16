package com.api.hotifi.payment.web.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SellerReviewsResponse {

    private long totalReviews;

    private long totalRatings;

    private String averageRating;

    private List<Long> eachRatings;

}
