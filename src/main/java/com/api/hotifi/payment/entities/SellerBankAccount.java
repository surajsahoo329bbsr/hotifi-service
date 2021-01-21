package com.api.hotifi.payment.entities;

import com.api.hotifi.identity.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"bankIfscCode", "bankAccountNumber"})
)
public class SellerBankAccount {

    @JsonIgnore
    @OneToOne(mappedBy = "sellerBankAccount")
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(System.currentTimeMillis());

    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isVerified;

    @Column(unique = true)
    private String linkedAccountId;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private String bankAccountType;

    @Column(nullable = false)
    private String bankAccountNumber;

    @Column(length = 11,nullable = false)
    private String bankIfscCode;

    @Column(nullable = false)
    private String bankBeneficiaryName;

    @Column(nullable = false)
    private String errorDescription;
}
