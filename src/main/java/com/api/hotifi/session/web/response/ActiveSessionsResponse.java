package com.api.hotifi.session.web.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ActiveSessionsResponse {
    //No validations to be done as input has not to be given by user
    private String username;

    private String userPhotoUrl;

    private double rating;

    private double downloadSpeed;

    private double uploadSpeed;

    private double price;

    private double data;

}
