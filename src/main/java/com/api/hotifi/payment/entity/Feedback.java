package com.api.hotifi.payment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
public class Feedback implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", referencedColumnName = "id")
    private Purchase purchase;

    @Column(precision = 1, scale = 1)
    private float rating;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isWifiSlow = false;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isWifiStopped = false;
}
