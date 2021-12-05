package com.api.hotifi.offer.services.interfaces;

import com.api.hotifi.offer.entities.Offer;
import com.api.hotifi.offer.web.requests.OfferRequest;
import com.api.hotifi.offer.web.responses.OfferResponse;

import java.util.List;

public interface IOfferService {

    void addOffer(OfferRequest offerRequest, String adminEmail);

    void updateOffer(OfferRequest offerRequest, String adminEmail, Long offerId);

    List<Offer> findAllOffers(int page, int size, boolean isDescending);

    List<OfferResponse> findAllActiveOffers();

    void activateOffer(Long offerId, String adminEmail);

    void deactivateOfferBeforeExpiry(Long offerId, String adminEmail, String deactivateBeforeExpiryReason);

}
