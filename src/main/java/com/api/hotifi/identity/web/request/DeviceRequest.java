package com.api.hotifi.identity.web.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class DeviceRequest {

    @Digits(integer = 18, fraction = 0)
    private Long userId;

    @NotBlank
    @Length(max = 255, message = "{device.name.invalid}")
    private String deviceName;

    @NotBlank
    @Length(max = 255, message = "{device.token.invalid}")
    private String token;

}
