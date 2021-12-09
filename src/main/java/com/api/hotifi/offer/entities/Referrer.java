package com.api.hotifi.offer.entities;

import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.jfr.Enabled;
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
public class Referrer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(columnDefinition = "INT", nullable = false)
    private int referralCount; //successful referral counts

    @Column(nullable = false)
    private String referralCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt = new Timestamp(System.currentTimeMillis());

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    @OneToMany(mappedBy = "referrer", fetch = FetchType.LAZY)
    private List<Referent> referents;

}
