package com.api.hotifi.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "session_id", nullable = false)
    private Session session;

    @Column(columnDefinition = "INT", nullable = false)
    private int status;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "buyer_id", nullable = false)
    private User user;

    @Column(length = 20, nullable = false)
    private String macAddress;

    @Column(precision = 15, scale = 3, nullable = false)
    private double data;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private Date createdAt = new Timestamp(System.currentTimeMillis());

    @Column(columnDefinition = "DATETIME")
    private Date sessionStartedAt;

    @Column(columnDefinition = "DATETIME")
    private Date sessionFinishedAt;

    @Column(precision = 10, scale = 2, nullable = false)
    private double amountPaid;

    @Column(precision = 10, scale = 2)
    private double amountRefund;

    @Column(precision = 1)
    private double rating;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(columnDefinition = "TINYINT")
    private boolean lowSpeed;

    @Column(columnDefinition = "TINYINT")
    private boolean wifiInterrupt;

}
