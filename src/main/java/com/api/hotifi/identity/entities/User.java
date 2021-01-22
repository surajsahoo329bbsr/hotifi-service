package com.api.hotifi.identity.entities;

import com.api.hotifi.payment.entities.SellerBankAccount;
import com.api.hotifi.speed_test.entity.SpeedTest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class User implements Serializable {

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_device",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "device_id", referencedColumnName = "id")}
    )
    Set<Device> userDevices;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_bank_account_id", referencedColumnName = "id")
    private SellerBankAccount sellerBankAccount;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isLoggedIn = true;

    @Column(length = 20, unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String facebookId;

    @Column(unique = true)
    private String googleId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authentication_id", referencedColumnName = "id", unique = true, nullable = false)
    private Authentication authentication;

    @Length(max = 2043, message = "{photo.url.max.length}")
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

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<SpeedTest> speedTests;
}
