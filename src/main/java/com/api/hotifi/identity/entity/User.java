package com.api.hotifi.identity.entity;

import com.api.hotifi.speed_test.entity.SpeedTest;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Getter
@Setter
@Entity
public class User implements Serializable {

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_device",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "device_id", referencedColumnName = "id")}
    )
    Set<Device> userDevices = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isLoggedIn = true;

    @Column(length = 20, unique = true, nullable = false)
    private String username;

    private String upiId;

    @Column(unique = true)
    private String facebookId;

    @Column(unique = true)
    private String googleId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id", referencedColumnName = "id", unique = true, nullable = false)
    private Authentication authentication;

    private String photoUrl;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dateOfBirth;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt = new Timestamp(System.currentTimeMillis());

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<SpeedTest> speedTests = new ArrayList<>();
}
