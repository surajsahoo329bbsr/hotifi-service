package com.api.hotifi.payment.repositories;

import com.api.hotifi.payment.entities.SellerReceipt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerReceiptRepository extends PagingAndSortingRepository<SellerReceipt, Long> {

    @Query(value = "SELECT * FROM seller_receipt WHERE seller_payment_id = ?1", nativeQuery = true)
    List<SellerReceipt> findSellerReceipts(Long sellerPaymentId, Pageable pageable);

    SellerReceipt findByTransferId(String transferId);

}
