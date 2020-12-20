package com.api.hotifi.speed_test.web.request;

import com.api.hotifi.common.constant.Constants;
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

    @NotBlank(message = "{network.name.blank}")
    @Length(max = 255, message = "{network.name.invalid}")
    private String networkName;

    //both upload and download speeds in MBs
    @DecimalMin(value = Constants.MINIMUM_UPLOAD_SPEED, message = "{upload.speed.minimum.invalid}")
    @Digits(integer = 15, fraction = 2, message = "{upload.speed.format.invalid}")
    private double uploadSpeed;

    @DecimalMin(value = Constants.MINIMUM_DOWNLOAD_SPEED, message = "{upload.speed.minimum.invalid}")
    @Digits(integer = 15, fraction = 2, message = "{upload.speed.format.invalid}")
    private double downloadSpeed;

    @Range(min = 1, message = "{user.id.invalid}")
    private Long userId;

    @NotBlank(message = "{pincode.blank}")
    @Length(max = 12, message = "{pincode.length.invalid}")
    private String pinCode;

}
