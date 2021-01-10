package com.api.hotifi.session.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Buyer {

    public String username;

    public String photoUrl;

    public String macAddress;

    public double dataUsed;

    public double dataBought;

    public Date sessionCreatedAt;

    public Date sessionModifiedAt;

    public Date sessionFinishedAt;

    public double amountPaid;

}
