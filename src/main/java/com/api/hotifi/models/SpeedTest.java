package com.api.hotifi.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class SpeedTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String networkName;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private Date createdAt = new Timestamp(System.currentTimeMillis());

    @Column(precision = 10, scale = 2, nullable = false)
    private double uploadSpeed;

    @Column(precision = 10, scale = 2, nullable = false)
    private double downloadSpeed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 12, nullable = false)
    private String pinCode;

    @OneToMany(mappedBy = "speed_test")
    private Set<Session> sessionSet;

}
