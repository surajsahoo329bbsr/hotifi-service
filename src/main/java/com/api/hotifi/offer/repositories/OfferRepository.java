package com.api.hotifi.offer.repositories;

import com.api.hotifi.offer.entities.Offer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends PagingAndSortingRepository<Offer, Long> {
}
