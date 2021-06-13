package com.api.hotifi.payment.repositories;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.payment.entities.Purchase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PurchaseRepository extends PagingAndSortingRepository<Purchase, Long> {

    Purchase findByPaymentId(String paymentId);

    @Query(value = "SELECT * FROM purchase WHERE buyer_id = ?1", nativeQuery = true)
    List<Purchase> findPurchasesByBuyerId(Long buyerId, Pageable pageable);

    @Query(value = "SELECT * FROM purchase WHERE session_id IN :session_ids", nativeQuery = true)
    List<Purchase> findPurchasesBySessionIds(@Param("session_ids") List<Long> sessionIds);

    @Modifying
    @Query(value = "UPDATE purchase SET status = ?1, refund_payment_id = ?2, refund_started_at = ?3, refund_transaction_id = ?4 WHERE id = ?5", nativeQuery = true)
    void updatePurchaseRefundStatus(int status, String refundPaymentId, Date refundDoneAt, String refundTransactionId, Long purchaseId);
}
