package com.api.hotifi.payment.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
public class Feedback implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", referencedColumnName = "id", unique = true, nullable = false)
    private Purchase purchase;

    @Column(precision = 1, scale = 1, nullable = false)
    private float rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    @Temporal(TemporalType.TIME)
    private Date createdAt = new Date(System.currentTimeMillis());

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isWifiSlow = false;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isWifiStopped = false;
}
