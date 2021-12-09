package com.api.hotifi.offer.services.interfaces;

import com.api.hotifi.offer.entities.Referrer;

public interface IReferrerService {

    String addReferral(Long userId);

    void verifyReferral(String referrerCode);

    boolean isValidReferralUserId(Long userId);

    Referrer getReferral(Long referrerUserId);

}
