package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.errors.UserStatusErrorCodes;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.Feedback;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.error.FeedbackErrorCodes;
import com.api.hotifi.payment.repositories.FeedbackRepository;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.services.interfaces.IFeedbackService;
import com.api.hotifi.payment.web.request.FeedbackRequest;
import com.api.hotifi.payment.web.responses.FeedbackResponse;
import com.api.hotifi.payment.web.responses.SellerReviewsResponse;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.repository.SessionRepository;
import com.api.hotifi.speedtest.entity.SpeedTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FeedbackServiceImpl implements IFeedbackService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PurchaseRepository purchaseRepository;
    private final FeedbackRepository feedbackRepository;

    public FeedbackServiceImpl(UserRepository userRepository, SessionRepository sessionRepository, PurchaseRepository purchaseRepository, FeedbackRepository feedbackRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.purchaseRepository = purchaseRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    @Override
    public void addFeedback(FeedbackRequest feedbackRequest) {
        Purchase purchase = purchaseRepository.findById(feedbackRequest.getPurchaseId()).orElse(null);
        if (purchase == null)
            throw new HotifiException(FeedbackErrorCodes.PURCHASE_NOT_FOUND_FOR_FEEDBACK);
        try {
            Feedback feedback = new Feedback();
            feedback.setComment(feedbackRequest.getComment());
            feedback.setRating(feedbackRequest.getRating());
            feedback.setComment(feedbackRequest.getComment());
            feedback.setWifiSlow(feedbackRequest.isWifiSlow());
            feedback.setWifiStopped(feedbackRequest.isWifiStopped());
            feedback.setPurchase(purchase);
            feedbackRepository.save(feedback);
        } catch (DataIntegrityViolationException e) {
            log.error("Feedback already given ", e);
            throw new HotifiException(FeedbackErrorCodes.FEEDBACK_ALREADY_GIVEN);
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(FeedbackErrorCodes.UNEXPECTED_FEEDBACK_ERROR);
        }
    }

    @Transactional
    @Override
    public Feedback getPurchaseFeedback(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
        if (purchase == null)
            throw new HotifiException(FeedbackErrorCodes.FEEDBACK_NOT_FOUND_FOR_NON_EXISTENT_PURCHASE);
        return feedbackRepository.findFeedbackByPurchaseId(purchaseId);
    }

    @Transactional
    @Override
    public List<FeedbackResponse> getSellerFeedbacks(Long sellerId, int page, int size, boolean isDescending) {
        User seller = userRepository.findById(sellerId).orElse(null);
        if (seller == null || seller.getAuthentication().isDeleted())
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        try {
            List<Feedback> feedbacks = getFeedbacksFromSeller(seller, page, size, isDescending);
            if (feedbacks == null)
                return null;
            List<FeedbackResponse> feedbackResponses = new ArrayList<>();
            feedbacks.forEach(feedback -> {
                User user = feedback.getPurchase().getSession().getSpeedTest().getUser();
                String sellerName = user.getFirstName() + " " + user.getLastName();
                String sellerPhotoUrl = user.getPhotoUrl();
                FeedbackResponse feedbackResponse = new FeedbackResponse();
                feedbackResponse.setFeedback(feedback);
                feedbackResponse.setSellerName(sellerName);
                feedbackResponse.setSellerPhotoUrl(sellerPhotoUrl);
                feedbackResponses.add(feedbackResponse);
            });
            return feedbackResponses;

        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new HotifiException(FeedbackErrorCodes.UNEXPECTED_FEEDBACK_ERROR);
        }
    }

    //No need for try catch exceptions
    @Transactional
    @Override
    public String getAverageRating(Long sellerId) {
        User seller = userRepository.findById(sellerId).orElse(null);
        if (seller == null) return null;
        OptionalDouble optionalDoubleRating = getFeedbacksFromSeller(seller, 0, Integer.MAX_VALUE, true)
                .stream().mapToDouble(Feedback::getRating).average();
        if (optionalDoubleRating.isEmpty()) return null;
        double rating = optionalDoubleRating.orElseThrow(IllegalStateException::new);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(rating);
    }

    @Transactional
    @Override
    public SellerReviewsResponse getSellerRatingDetails(Long sellerId) {
        User seller = userRepository.findById(sellerId).orElse(null);
        if (seller == null || seller.getAuthentication().isDeleted())
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        try {
            List<Feedback> feedbacks = getFeedbacksFromSeller(seller, 0, Integer.MAX_VALUE, true);
            String getAverageRating = getAverageRating(sellerId);
            String averageRating = getAverageRating != null ? getAverageRating : "0.0";
            long totalReviews = feedbacks != null ? feedbacks.stream()
                    .filter(feedback -> feedback.getComment() != null)
                    .count() : 0;
            long totalRatings = feedbacks != null ? (long) feedbacks.size() : 0;
            Supplier<Stream<Feedback>> feedbackSupplier = feedbacks != null ? feedbacks::stream : null;

            long oneStarCount = feedbackSupplier != null ? feedbackSupplier.get()
                    .filter(getFeedbackPredicate(1.0F, 1.0F)).count() : 0;
            long twoStarCount = feedbackSupplier != null ? feedbackSupplier.get()
                    .filter(getFeedbackPredicate(1.5F, 2.0F)).count() : 0;
            long threeStarCount = feedbackSupplier != null ? feedbackSupplier.get()
                    .filter(getFeedbackPredicate(2.5F, 3.0F)).count() : 0;
            long fourStarCount = feedbackSupplier != null ? feedbackSupplier.get()
                    .filter(getFeedbackPredicate(3.5F, 4.0F)).count() : 0;
            long fiveStarCount = feedbackSupplier != null ? feedbackSupplier.get()
                    .filter(getFeedbackPredicate(4.5F, 5.0F)).count() : 0;

            List<Long> eachRatings = Arrays.asList(oneStarCount, twoStarCount, threeStarCount, fourStarCount, fiveStarCount);

            return new SellerReviewsResponse(totalReviews, totalRatings, averageRating, eachRatings);

        } catch (Exception e) {
            log.error("Error Occurred", e);
            throw new HotifiException(UserStatusErrorCodes.UNEXPECTED_USER_STATUS_ERROR);
        }
    }

    public List<Feedback> getFeedbacksFromSeller(User seller, int page, int size, boolean isDescending) {
        Pageable pageable = isDescending ? PageRequest.of(page, size, Sort.by("created_at").descending())
                : PageRequest.of(page, size, Sort.by("created_at"));
        Pageable pageableAll = isDescending ? PageRequest.of(0, Integer.MAX_VALUE, Sort.by("created_at").descending())
                : PageRequest.of(page, size, Sort.by("created_at"));
        List<Long> speedTestIds = seller.getSpeedTests()
                .stream().map(SpeedTest::getId)
                .collect(Collectors.toList());
        List<Long> sessionIds = sessionRepository.findSessionsBySpeedTestIds(speedTestIds, pageableAll)
                .stream().map(Session::getId)
                .collect(Collectors.toList());
        List<Long> purchaseIds = purchaseRepository.findPurchasesBySessionIds(sessionIds)
                .stream().map(Purchase::getId)
                .collect(Collectors.toList());
        return feedbackRepository.findFeedbacksByPurchaseIds(purchaseIds, pageable);
    }

    public Predicate<Feedback> getFeedbackPredicate(Float firstRating, Float secondRating) {
        return feedback -> Float.compare(feedback.getRating(), firstRating) == 0 || Float.compare(feedback.getRating(), secondRating) == 0;
    }
}
