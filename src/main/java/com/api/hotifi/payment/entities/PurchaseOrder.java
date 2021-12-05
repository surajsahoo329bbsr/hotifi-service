package com.api.hotifi.payment.entities;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.session.entity.Session;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class PurchaseOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    //Below 2 attributes are of money transaction
    @Column(nullable = false)
    private BigDecimal amount; //This can be zero

    @Column(nullable = false)
    private BigDecimal amountPaid; //This can be zero

    @Column(nullable = false)
    private BigDecimal amountDue; //This can be zero

    //Below 2 attributes are because of offers
    @Column(nullable = false)
    private BigDecimal amountTotal; // This is always more than zero

    @Column(nullable = false)
    private BigDecimal amountDiscount; /** This is cannot be more than amountTotal  **/

    @Column(columnDefinition = "INT", nullable = false)
    private int data;

    @Column(nullable = false, columnDefinition = "INT")
    private int attempts;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderCreatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderModifiedAt;

    @Column(nullable = false)
    private String status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "purchaseOrder", fetch = FetchType.LAZY)
    private List<Purchase> purchases;

}
