package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.payment.web.responses.BuyerStatsResponse;
import com.api.hotifi.payment.web.responses.SellerReviewsResponse;
import com.api.hotifi.payment.web.responses.SellerStatsResponse;

public interface IStatsService {

    BuyerStatsResponse getBuyerStats(Long buyerId);

    SellerStatsResponse getSellerStats(Long sellerId);
}
