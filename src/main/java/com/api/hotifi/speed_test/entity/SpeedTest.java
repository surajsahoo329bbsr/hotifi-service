package com.api.hotifi.speed_test.entity;

import com.api.hotifi.identity.entity.User;
import com.api.hotifi.session.entity.Session;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class SpeedTest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String networkName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
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

    @OneToMany(mappedBy = "speedTest")
    private List<Session> sessions = new ArrayList<>();

}
