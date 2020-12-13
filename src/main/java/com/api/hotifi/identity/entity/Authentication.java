package com.api.hotifi.identity.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Entity
public class Authentication implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 7)
    private String countryCode;

    @Column(length = 15, unique = true)
    private String phone;

    @Column(nullable = false)
    private String token;

    private String emailOtp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date tokenCreatedAt = new Timestamp(System.currentTimeMillis());

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isEmailVerified = false;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isPhoneVerified = false;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isActivated = false;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isFreezed = false;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isBanned = false;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isDeleted = false;

}
