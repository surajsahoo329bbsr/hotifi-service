package com.api.hotifi.payment.entities;

import com.api.hotifi.identity.entities.Authentication;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
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
    private BigDecimal amountPaid;

    private String upiTransactionId; //The Id we get from third party vendors like GPay, PhonePe, Paytm

    private String utr;

    private String upiId; //seller's UPI id

    private String transferId; //For Payment Gateways

    private String settlementId; //For Payment Gateways

    private String transferTransactionId; //For Payment Gateways

    private String bankAccountNumber; //For Payment Gateways

    private String bankIfscCode; //For Payment Gateways

    @Column(columnDefinition = "INT", nullable = false)
    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt = new Date(System.currentTimeMillis());

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
    private Authentication createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modified_by", referencedColumnName = "id")
    private Authentication modifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paidAt; // Time at which amount is paid to the seller

}
