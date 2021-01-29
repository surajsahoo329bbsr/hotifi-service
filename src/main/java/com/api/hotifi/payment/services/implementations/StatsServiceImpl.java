package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.UserStatusErrorCodes;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.error.PurchaseErrorCodes;
import com.api.hotifi.payment.error.SellerPaymentErrorCodes;
import com.api.hotifi.payment.processor.codes.BuyerPaymentCodes;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class StatsServiceImpl implements IStatsService {

    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final SessionRepository sessionRepository;
    private final SellerPaymentRepository sellerPaymentRepository;
    private final IFeedbackService feedbackService;

    public StatsServiceImpl(UserRepository userRepository, PurchaseRepository purchaseRepository, SessionRepository sessionRepository, SellerPaymentRepository sellerPaymentRepository, IFeedbackService feedbackService) {
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
        this.sessionRepository = sessionRepository;
        this.sellerPaymentRepository = sellerPaymentRepository;
        this.feedbackService = feedbackService;
    }

    @Transactional(readOnly = true)
    @Override
    public BuyerStatsResponse getBuyerStats(Long buyerId) {
        try {
            User buyer = userRepository.findById(buyerId).orElse(null);
            if (LegitUtils.isUserLegit(buyer)) {
                Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("session_created_at").descending());
                Supplier<Stream<Purchase>> purchaseStreamSupplier = () -> purchaseRepository.findPurchasesByBuyerId(buyerId, pageable).stream();
                double totalPendingRefunds = purchaseStreamSupplier.get().filter(purchase -> purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE < BuyerPaymentCodes.REFUND_STARTED.value())
                        .mapToDouble(Purchase::getAmountRefund)
                        .sum();

                double totalDataBought = purchaseStreamSupplier.get().mapToDouble(Purchase::getDataUsed).sum();
                double totalDataBoughtByWifi = purchaseStreamSupplier.get().filter(purchase -> purchase.getSession().getSpeedTest().getNetworkName().equals("WIFI"))
                        .mapToDouble(Purchase::getDataUsed)
                        .sum();
                double totalDataBoughtByMobile = totalDataBought - totalDataBoughtByWifi;
                return new BuyerStatsResponse(totalPendingRefunds, totalDataBought, totalDataBoughtByWifi, totalDataBoughtByMobile);
            } else
                throw new HotifiException(PurchaseErrorCodes.BUYER_NOT_LEGIT);
        } catch (Exception e) {
            log.error("Error Occurred", e);
            throw new HotifiException(UserStatusErrorCodes.UNEXPECTED_USER_STATUS_ERROR);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public SellerStatsResponse getSellerStats(Long sellerId) {
        User seller = userRepository.findById(sellerId).orElse(null);
        if (!LegitUtils.isSellerLegit(seller, false))
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_NOT_LEGIT);
        SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerId);
        if (sellerPayment == null)
            throw new HotifiException(SellerPaymentErrorCodes.NO_SELLER_PAYMENT_EXISTS);
        try {
            double totalEarnings = Math.floor(sellerPayment.getAmountEarned() * (double) (100 - Constants.COMMISSION_PERCENTAGE) / 100);
            double totalAmountWithdrawn = sellerPayment.getAmountPaid();
            String averageRating = feedbackService.getAverageRating(sellerId);
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("created_at").descending());

            List<Long> speedTestIds = seller.getSpeedTests()
                    .stream().map(SpeedTest::getId)
                    .collect(Collectors.toList());
            List<Long> sessionIds = sessionRepository.findSessionsBySpeedTestIds(speedTestIds, pageable)
                    .stream().map(Session::getId)
                    .collect(Collectors.toList());
            Supplier<Stream<Purchase>> purchaseStreamSupplier = () -> purchaseRepository.findPurchasesBySessionIds(sessionIds).stream();

            double totalDataSold = purchaseStreamSupplier.get().mapToDouble(Purchase::getDataUsed).sum();
            double totalDataSoldByWifi = purchaseStreamSupplier.get().filter(purchase -> purchase.getSession().getSpeedTest().getNetworkName().equals("WIFI"))
                    .mapToDouble(Purchase::getDataUsed)
                    .sum();
            double totalDataSoldByMobile = totalDataSold - totalDataSoldByWifi;
            return new SellerStatsResponse(sellerPayment.getId(), sellerPayment.getLastPaidAt(),totalEarnings, totalAmountWithdrawn, averageRating,totalDataSold, totalDataSoldByWifi, totalDataSoldByMobile);
        } catch (Exception e) {
            log.error("Error Occurred", e);
            throw new HotifiException(UserStatusErrorCodes.UNEXPECTED_USER_STATUS_ERROR);
        }
    }
}
