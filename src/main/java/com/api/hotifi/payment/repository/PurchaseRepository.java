package com.api.hotifi.payment.repository;

import com.api.hotifi.payment.entity.Purchase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends PagingAndSortingRepository<Purchase, Long> {

    @Query(value = "SELECT * FROM purchase WHERE buyer_id = ?1", nativeQuery = true)
    List<Purchase> findPurchasesByBuyerId(Long buyerId, Pageable pageable);
}
