package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.repositories.SellerPaymentRepository;
import com.api.hotifi.payment.services.interfaces.IFeedbackService;
import com.api.hotifi.payment.services.interfaces.IStatsService;
import com.api.hotifi.payment.web.responses.BuyerStatsResponse;
import com.api.hotifi.payment.web.responses.SellerStatsResponse;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.repository.SessionRepository;
import com.api.hotifi.speed_test.entity.SpeedTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class StatsServiceImpl implements IStatsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SellerPaymentRepository sellerPaymentRepository;

    @Autowired
    private IFeedbackService feedbackService;

    @Transactional(readOnly = true)
    @Override
    public BuyerStatsResponse getBuyerStats(Long buyerId) {
        try {
            User buyer = userRepository.findById(buyerId).orElse(null);
            if (LegitUtils.isUserLegit(buyer)) {
                Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
                Stream<Purchase> purchaseStream = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable).stream();
                double totalPendingRefunds = purchaseStream.filter(purchase -> purchase.getStatus() == 4) //TODO add status check
                        .mapToDouble(Purchase::getAmountRefund)
                        .sum();
                double totalDataBought = purchaseStream.mapToDouble(Purchase::getAmountRefund).sum();
                double totalDataBoughtByWifi = purchaseStream.filter(purchase -> purchase.getSession().getSpeedTest().getNetworkName().equals("WIFI"))
                        .mapToDouble(Purchase::getDataUsed)
                        .sum();
                double totalDataBoughtByMobile = totalDataBought - totalDataBoughtByWifi;
                return new BuyerStatsResponse(totalPendingRefunds, totalDataBought, totalDataBoughtByWifi, totalDataBoughtByMobile);
            }
        } catch (Exception e) {
            log.error("Error Occurred", e);
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public SellerStatsResponse getSellerStats(Long sellerId) {
        try {
            User seller = userRepository.findById(sellerId).orElse(null);
            if (!LegitUtils.isSellerLegit(seller))
                throw new Exception("Seller is not legit to withdraw money");
            SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerId);
            if (sellerPayment == null)
                throw new Exception("Seller Payment doesn't exist");
            double totalEarnings = Math.floor(sellerPayment.getAmountEarned() * (double) (100 - Constants.COMMISSION_PERCENTAGE) / 100);
            double totalAmountWithdrawn = sellerPayment.getAmountPaid();
            String averageRating = feedbackService.getAverageRating(sellerId);

            List<Long> speedTestIds = seller.getSpeedTests()
                    .stream().map(SpeedTest::getId)
                    .collect(Collectors.toList());
            List<Long> sessionIds = sessionRepository.findSessionsBySpeedTestIds(speedTestIds)
                    .stream().map(Session::getId)
                    .collect(Collectors.toList());
            List<Purchase> purchases = purchaseRepository.findPurchasesBySessionIds(sessionIds);

            double totalDataSold = purchases.stream().mapToDouble(Purchase::getDataUsed).sum();
            double totalDataSoldByWifi = purchases.stream().filter(purchase -> purchase.getSession().getSpeedTest().getNetworkName().equals("WIFI"))
                    .mapToDouble(Purchase::getDataUsed)
                    .sum();
            double totalDataSoldByMobile = totalDataSold - totalDataSoldByWifi;
            return new SellerStatsResponse(totalEarnings, totalAmountWithdrawn, averageRating,totalDataSold, totalDataSoldByWifi, totalDataSoldByMobile);
        } catch (Exception e) {
            log.error("Error Occurred", e);
        }
        return null;
    }
}
