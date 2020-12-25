package com.api.hotifi.payment.entity;

import com.api.hotifi.identity.entities.User;
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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", unique = true, nullable = false)
    private User seller;

    @Column(precision = 10, scale = 2, nullable = false)
    private double amountEarned; // Total amount earned by the seller

    @Column(precision = 10, scale = 2, nullable = false)
    private double amountPaid; // Total amount to be paid to the seller

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(System.currentTimeMillis()); // Time at which first money was deposited

    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt; // Time at which money started to accumulate

    @Temporal(TemporalType.TIMESTAMP)
    private Date paidAt; // Time at which amount is paid to the seller

}
