package com.api.hotifi.session.entity;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.payment.entities.PurchaseOrder;
import com.api.hotifi.speedtest.entity.SpeedTest;
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
public class Session implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "speed_test_id", nullable = false)
    private SpeedTest speedTest;

    @Column(nullable = false)
    private String wifiPassword;

    //Data in MB
    @Column(columnDefinition = "INT", nullable = false)
    private int data;

    //Data left in MB
    @Column(columnDefinition = "DECIMAL(10,3)", nullable = false)
    private double dataUsed = 0.0;

    @Column(length = 10, nullable = false)
    private String currency = BusinessConfigurations.CURRENCY_INR;

    @Column(nullable = false)
    private BigDecimal price;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt = new Date(System.currentTimeMillis());

    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedAt;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    private List<PurchaseOrder> purchaseOrders;

}
