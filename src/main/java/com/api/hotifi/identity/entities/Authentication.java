package com.api.hotifi.identity.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Authentication implements Serializable {

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "authentication_roles",
            joinColumns = @JoinColumn(name = "authentication_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 7)
    private String countryCode;

    @Column(length = 15, unique = true)
    private String phone;

    private String emailOtp;

    @Column(nullable = false)
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt = new Timestamp(System.currentTimeMillis());

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date modifiedAt;

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
