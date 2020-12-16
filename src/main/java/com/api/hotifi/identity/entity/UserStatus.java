package com.api.hotifi.identity.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(length = 20, nullable = false)
    private String role; //seller or buyer

    @Temporal(TemporalType.TIMESTAMP)
    private Date warningCreatedAt;

    private String warningReason;

    @Temporal(TemporalType.TIMESTAMP)
    private Date freezeCreatedAt;

    private String freezeReason;

    @Column(columnDefinition = "INT")
    private int freezePeriod; //In Days

    private Date banCreatedAt;

    private String banReason;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    private String deletedReason;

}
