package com.api.hotifi.payment.repositories;

import com.api.hotifi.payment.entities.Feedback;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends PagingAndSortingRepository<Feedback, Long> {

    @Query(value = "SELECT * FROM feedback WHERE purchase_id = ?1", nativeQuery = true)
    Feedback findFeedbackByPurchaseId(Long purchaseId);

    @Query(value = "SELECT * FROM feedback WHERE purchase_id IN : purchase_ids", nativeQuery = true)
    List<Feedback> findFeedbacksByPurchaseIds(@Param("purchase_ids") List<Long> purchaseIds, Pageable pageable);
}
