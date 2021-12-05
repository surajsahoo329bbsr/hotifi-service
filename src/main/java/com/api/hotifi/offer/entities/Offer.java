package com.api.hotifi.offer.entities;

import com.api.hotifi.identity.entities.Authentication;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Offer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "TINYINT")
    private boolean isActive;

    @Column(nullable = false, length = 12)
    private String promoCode;

    @Column(nullable = false)
    private BigDecimal priceBudget;

    @Column(nullable = false, columnDefinition = "INT")
    private int commissionPercentage;

    @Column(nullable = false, columnDefinition = "INT")
    private int discountPercentage;

    @Column(nullable = false, columnDefinition = "INT")
    private int maximumDiscountAmount;

    @Column(nullable = false, columnDefinition = "INT")
    private int minimumReferrals;

    @Column(nullable = false, columnDefinition = "INT")
    private int offerUseCount;

    @Column(nullable = false)
    private String offerType;

    private String terms;

    private String deactivateBeforeExpiryReason;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startsAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt = new Timestamp(System.currentTimeMillis());

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
    private Authentication createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modified_by", referencedColumnName = "id", unique = true)
    private Authentication modifiedBy;

    @JsonIgnore
    @OneToMany(mappedBy = "offer", fetch = FetchType.LAZY)
    private List<Referrer> referrers;

}
