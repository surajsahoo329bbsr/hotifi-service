package com.api.hotifi.payment.entities;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.session.entity.Session;
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
public class Purchase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentId;

    private String refundPaymentId;

    private String paymentTransactionId;

    private String refundTransactionId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(columnDefinition = "INT", nullable = false)
    private int status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User user;

    @Column(length = 20)
    private String macAddress;

    @Column(length = 45)
    private String ipAddress;

    @Column(columnDefinition = "INT", nullable = false)
    private int data;

    @Column(columnDefinition = "Decimal(10,3)", nullable = false)
    private double dataUsed;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date paymentDoneAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sessionCreatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sessionModifiedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sessionFinishedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date refundStartedAt;

    @Column(nullable = false)
    private BigDecimal amountPaid;

    @Column(nullable = false)
    private BigDecimal amountRefund;

}
