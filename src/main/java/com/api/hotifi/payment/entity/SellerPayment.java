package com.api.hotifi.payment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
public class SellerPayment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @Column(precision = 10, scale = 2, nullable = false)
    private double amountEarned; // Total amount earned by the seller

    @Column(precision = 10, scale = 2, nullable = false)
    private double amountPaid; // Total amount to be paid to the seller

    @Temporal(TemporalType.TIMESTAMP)
    private Date paidAt; // Time at which amount is paid to the seller

}
