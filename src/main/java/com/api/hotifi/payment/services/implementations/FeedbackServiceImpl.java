package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.Feedback;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.error.FeedbackErrorCodes;
import com.api.hotifi.payment.repositories.FeedbackRepository;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.services.interfaces.IFeedbackService;
import com.api.hotifi.payment.web.request.FeedbackRequest;
import com.api.hotifi.payment.web.responses.FeedbackResponse;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.repository.SessionRepository;
import com.api.hotifi.speed_test.entity.SpeedTest;
import com.api.hotifi.speed_test.repository.SpeedTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FeedbackServiceImpl implements IFeedbackService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpeedTestRepository speedTestRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Transactional
    @Override
    public void addFeedback(FeedbackRequest feedbackRequest) {
        Purchase purchase = purchaseRepository.findById(feedbackRequest.getPurchaseId()).orElse(null);
        if (purchase == null)
            throw new HotifiException(FeedbackErrorCodes.NO_PURCHASE_NO_FEEDBACK);
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
            throw new HotifiException(FeedbackErrorCodes.NO_FEEDBACK_EXISTS_FOR_NO_PURCHASE);
        return feedbackRepository.findFeedbackByPurchaseId(purchaseId);
    }

    @Transactional
    @Override
    public List<FeedbackResponse> getSellerFeedbacks(Long sellerId, int page, int size, boolean isDescending) {
        try {
            User seller = userRepository.findById(sellerId).orElse(null);
            List<Long> speedTestIds = seller != null ?
                    seller.getSpeedTests()
                            .stream().map(SpeedTest::getId)
                            .collect(Collectors.toList()) : null;
            List<Long> sessionIds = speedTestIds != null ?
                    sessionRepository.findSessionsBySpeedTestIds(speedTestIds)
                            .stream().map(Session::getId)
                            .collect(Collectors.toList()) : null;
            List<Long> purchaseIds = sessionIds != null ?
                    purchaseRepository.findPurchasesBySessionIds(sessionIds)
                            .stream().map(Purchase::getId)
                            .collect(Collectors.toList()) : null;

            Pageable pageable = isDescending ? PageRequest.of(page, size, Sort.by("created_at").descending())
                    : PageRequest.of(page, size, Sort.by("created_at"));

            List<Feedback> feedbacks = purchaseIds != null ?
                    feedbackRepository.findFeedbacksByPurchaseIds(purchaseIds, pageable) : null;
            if (feedbacks == null) return null;
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
        List<Long> speedTestIds = seller != null ?
                seller.getSpeedTests()
                        .stream().map(SpeedTest::getId)
                        .collect(Collectors.toList()) : null;
        List<Long> sessionIds = speedTestIds != null ?
                sessionRepository.findSessionsBySpeedTestIds(speedTestIds)
                        .stream().map(Session::getId)
                        .collect(Collectors.toList()) : null;
        List<Long> purchaseIds = sessionIds != null ?
                purchaseRepository.findPurchasesBySessionIds(sessionIds)
                        .stream().map(Purchase::getId)
                        .collect(Collectors.toList()) : null;

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("created_at").descending());

        OptionalDouble optionalDoubleRating = purchaseIds == null ? OptionalDouble.empty() :
                feedbackRepository.findFeedbacksByPurchaseIds(purchaseIds, pageable)
                        .stream().mapToDouble(Feedback::getRating).average();

        if (optionalDoubleRating.isEmpty())
            return null;

        double rating = optionalDoubleRating.orElseThrow(IllegalStateException::new);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(rating);
    }
}
