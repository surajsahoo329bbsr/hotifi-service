package com.api.hotifi.speed_test.web.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SpeedTestRequest {

    @NotBlank(message = "{network.name.blank}")
    @Length(max = 255, message = "{network.name.invalid}")
    private String networkName;

    //both upload and download speeds in MBs
    @Digits(integer = 15, fraction = 2)
    private double uploadSpeed;

    @Digits(integer = 15, fraction = 2)
    private double downloadSpeed;

    @Digits(integer = 18, fraction = 0)
    private Long userId;

    @NotBlank(message = "{pincode.blank}")
    @Length(max = 12, message = "{pincode.length.invalid}")
    private String pinCode;

}
