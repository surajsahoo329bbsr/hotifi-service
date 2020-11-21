package com.api.hotifi.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
public class User {

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_device",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "device_id")}
    )

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean isLoggedIn = true;

    @Column(length = 20, unique = true)
    private String username;

    private String upiId;

    @Column(unique = true)
    private String facebookId;

    @Column(unique = true)
    private String googleId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", referencedColumnName = "email")
    private EmailVerification emailVerification;

    @Column(length = 5, nullable = false)
    private String countryCode;

    @Column(length = 15, unique = true, nullable = false)
    private String phoneNumber;

    private String photoUrl;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private Date dateOfBirth;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private Date createdAt = new Timestamp(System.currentTimeMillis());

    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean isActivated = true;

    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean isFreezed = false;

    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean isBanned = false;

    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private String token;

    @Column(columnDefinition = "INT")
    private int emailOtp;

    @OneToMany(mappedBy = "user")
    private Set<SpeedTest> speedTestSet;
}
