package com.api.hotifi.identity.web.response;

import lombok.Builder;
import lombok.Setter;

import java.util.Date;

@Builder
@Setter
public class AuthenticationResponse {

    private long id;

    private String email;

    private String token;

    private String otp;

    private Date tokenCreatedAt;

    private boolean isVerified;

    private boolean isActivated;

    private boolean isFreezed;

    private boolean isBanned;

    private boolean isDeleted;

}

