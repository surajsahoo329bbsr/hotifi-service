package com.api.hotifi.payment.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
public class SellerReceipt implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_payment_id", nullable = false)
    private SellerPayment sellerPayment;

    @Column(nullable = false)
    private double amountPaid;

    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String bankAccountNumber;

    @Column(nullable = false)
    private String bankIfscCode;

    @Column(columnDefinition = "INT", nullable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt = new Date(System.currentTimeMillis());

    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paidAt; // Time at which amount is paid to the seller


}
