package com.api.hotifi.identity.entities;

import com.api.hotifi.payment.entities.Purchase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
public class UserStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    //Uncomment below to load json for Purchase in UserStatus
    //@JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", referencedColumnName = "id")
    //Uncomment below to load json for Purchase in UserStatus
    //@JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Purchase purchase;

    @Column(length = 20, nullable = false)
    private String role; //seller or buyer

    @Temporal(TemporalType.TIMESTAMP)
    private Date warningCreatedAt;

    private String warningReason;

    @Temporal(TemporalType.TIMESTAMP)
    private Date freezeCreatedAt;

    private String freezeReason;

    @Column(columnDefinition = "INT")
    private int freezePeriod; //In Hours

    private Date banCreatedAt;

    private String banReason;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    private String deletedReason;

}
