package com.api.hotifi.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.transaction.Transaction;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "speed_test_id", nullable = false)
    private SpeedTest speedTest;

    @Column(nullable = false)
    private String wifiPassword;

    @Column(columnDefinition = "INT", nullable = false)
    private int unitData;

    @Column(length = 10, nullable = false)
    private String currency = "INR";

    @Column(precision = 10, scale = 2, nullable = false)
    private double unitPrice;

    @Column(columnDefinition = "INT", nullable = false)
    private int maxUsers;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private Date startTime;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private Date endTime;

    @OneToMany(mappedBy = "session")
    private Set<Purchase> purchaseSet;

}
