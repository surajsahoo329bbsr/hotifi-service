package com.api.hotifi.speedtest.web.request;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.speedtest.validator.NetworkProvider;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SpeedTestRequest {

    @NetworkProvider
    private String networkProvider;

    //both upload and download speeds in MBs
    @DecimalMin(value = BusinessConfigurations.MINIMUM_UPLOAD_SPEED, message = "{upload.speed.minimum.invalid}")
    @Digits(integer = 15, fraction = 2, message = "{upload.speed.format.invalid}")
    private double uploadSpeed;

    @DecimalMin(value = BusinessConfigurations.MINIMUM_DOWNLOAD_SPEED, message = "{upload.speed.minimum.invalid}")
    @Digits(integer = 15, fraction = 2, message = "{upload.speed.format.invalid}")
    private double downloadSpeed;

    @Range(min = 1, message = "{user.id.invalid}")
    private Long userId;

    @NotBlank(message = "{pincode.blank}")
    @Length(max = 12, message = "{pincode.length.invalid}")
    private String pinCode;

}
