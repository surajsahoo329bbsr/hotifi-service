package com.api.hotifi.payment.repositories;

import com.api.hotifi.payment.entities.SellerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerPaymentRepository extends JpaRepository<SellerPayment, Long> {

    @Query(value = "SELECT * FROM seller_payment WHERE seller_id = ?1", nativeQuery = true)
    SellerPayment findSellerPaymentBySellerId(Long sellerId);
}
