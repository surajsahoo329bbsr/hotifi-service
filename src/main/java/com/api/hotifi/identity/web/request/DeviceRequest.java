package com.api.hotifi.identity.web.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class DeviceRequest {

    @Range(min = 1, message = "{user.id.invalid}")
    private Long userId;

    @NotBlank(message = "{android.id.blank}")
    @Length(max = 255, message = "{android.id.invalid}")
    private String androidId;

    @NotBlank(message = "{device.name.blank}")
    @Length(max = 1024, message = "{device.name.invalid}")
    private String deviceName;

    @NotBlank(message = "{token.blank}")
    @Length(max = 4096, message = "{token.invalid}")
    private String token;

}
