package com.api.hotifi.identity.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AvailabilityResponse {

    private Boolean isUsernameAvailable;

    private Boolean isPhoneAvailable;

    private Boolean isEmailAvailable;

}
