package com.api.hotifi.session.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class Buyer {

    public String username;

    public String photoUrl;

    public int status;

    public String macAddress;

    public String ipAddress;

    public double dataUsed;

    public double dataBought;

    public Date sessionCreatedAt;

    public Date sessionModifiedAt;

    public Date sessionFinishedAt;

    public BigDecimal amountPaid;

}
