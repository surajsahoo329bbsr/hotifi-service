package com.api.hotifi.session.web.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ActiveSessionsResponse {

    private Long sessionId;

    private Long sellerId;

    //No validations to be done as input has not to be given by user
    private String username;

    private String userPhotoUrl;

    private String averageRating;

    private String networkProvider;

    private double downloadSpeed;

    private double uploadSpeed;

    private BigDecimal price;

    private double data;

    private double longitude;

    private double latitude;

}
