package com.api.hotifi.offer.services.implementations;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.offer.entities.Referrer;
import com.api.hotifi.offer.errors.ReferrerErrorCodes;
import com.api.hotifi.offer.repositories.OfferRepository;
import com.api.hotifi.offer.repositories.ReferrerRepository;
import com.api.hotifi.offer.services.interfaces.IOfferService;
import com.api.hotifi.offer.services.interfaces.IReferrerService;
import com.api.hotifi.offer.utils.OfferUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class ReferrerServiceImpl implements IReferrerService {

    private final UserRepository userRepository;
    private final ReferrerRepository referrerRepository;
    private final IOfferService offerService;

    public ReferrerServiceImpl(ReferrerRepository referrerRepository, UserRepository userRepository, IOfferService offerService) {
        this.referrerRepository = referrerRepository;
        this.userRepository = userRepository;
        this.offerService = offerService;
    }

    @Override
    public String addOrUpdateReferral(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (LegitUtils.isUserLegit(user)) {
            Referrer referrer = referrerRepository.findLatestReferralByUserId(userId);
            Date now = new Date(System.currentTimeMillis());
            if(referrer == null){
                //If we don't get any referrer, then add referral
                String referralCode = OfferUtils.generateReferralCode(userId);

                //referrerRepository.save(referralCode);
                return referralCode;
            }

            //Update referral flow here
            boolean isCodeAlreadyGenerated = referrer.getExpiresAt().before(now) ||
                    referrer.getReferralCount() >= referrer.getOffer().getMinimumReferrals();
            //TODO add check for total referrals by a user id
            boolean isReferralOfferActive = referrer.getOffer().isActive();
            offerService.findAllActiveOffers();
            if(isCodeAlreadyGenerated){
                throw new HotifiException(ReferrerErrorCodes.REFERRAL_ALREADY_GENERATED);
            }
            if(isReferralOfferActive){
                throw new HotifiException(ReferrerErrorCodes.REFERRAL_OFFER_INACTIVE);
            }
            return OfferUtils.generateReferralCode(userId);
        }
        throw new HotifiException(ReferrerErrorCodes.UNEXPECTED_REFERRER_ERROR);
    }

    @Override
    public void verifyReferral(String referrerCode) {

    }

    @Override
    public boolean isValidReferralUserId(Long userId) {
        String referralCode = OfferUtils.generateReferralCode(userId);
        char[] code = referralCode.toCharArray();
        StringBuilder checkUserId = new StringBuilder();
        for (char ch : code) {
            if (Character.isDigit(ch)) {
                checkUserId.append(ch);
            }
        }
        Long checkedUserId = Long.parseLong(checkUserId.toString());
        return checkedUserId.compareTo(userId) == 0;
    }

    @Override
    public Referrer getReferral(Long referrerUserId) {
        return null;
    }
}
