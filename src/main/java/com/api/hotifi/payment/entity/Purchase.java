package com.api.hotifi.payment.entity;

import com.api.hotifi.session.entity.Session;
import com.api.hotifi.identity.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Entity
public class Purchase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(columnDefinition = "INT", nullable = false)
    private int status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User user;

    @Column(length = 20, nullable = false)
    private String macAddress;

    @Column(columnDefinition = "INT", nullable = false)
    private int data;

    @Column(precision = 15, scale = 3, nullable = false)
    private double dataUsed;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt = new Timestamp(System.currentTimeMillis());

    @Temporal(TemporalType.TIMESTAMP)
    private Date sessionStartedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sessionFinishedAt;

    @Column(precision = 10, scale = 2, nullable = false)
    private double amountPaid;

    @Column(precision = 10, scale = 2)
    private double amountRefund;
}
