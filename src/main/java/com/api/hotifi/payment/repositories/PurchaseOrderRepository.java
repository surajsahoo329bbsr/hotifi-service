package com.api.hotifi.payment.repositories;

import com.api.hotifi.payment.entities.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Query(value = "SELECT * FROM purchase_order WHERE order_id = ?1", nativeQuery = true)
    PurchaseOrder findPurchaseOrderByPurchaseOrderId(String orderId);

}
