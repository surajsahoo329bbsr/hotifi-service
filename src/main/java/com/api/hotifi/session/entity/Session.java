package com.api.hotifi.session.entity;

import com.api.hotifi.payment.entity.Purchase;
import com.api.hotifi.speed_test.entity.SpeedTest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Session implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "speed_test_id", nullable = false)
    private SpeedTest speedTest;

    @Column(nullable = false)
    private String wifiPassword;

    //Data in MB
    @Column(columnDefinition = "INT", nullable = false)
    private int data;

    //Data left in MB
    @Column(precision = 15, scale = 3, nullable = false)
    private double dataUsed = 0.0;

    @Column(length = 10, nullable = false)
    private String currency = "INR";

    @Column(precision = 10, scale = 2, nullable = false)
    private double price;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startedAt = new Date(System.currentTimeMillis());

    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedAt;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    private List<Purchase> purchases;

}
