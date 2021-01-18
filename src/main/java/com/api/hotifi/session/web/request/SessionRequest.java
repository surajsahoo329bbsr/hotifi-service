package com.api.hotifi.session.web.request;

import com.api.hotifi.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SessionRequest {

    @Range(min = 1, message = "{session.user.id.invalid}")
    private Long userId;

    @NotBlank(message = "{pincode.blank}")
    @Length(max = 12, message = "{pincode.length.invalid}")
    private String pinCode;

    @NotBlank(message = "{session.wifi.password.blank}")
    @Length(max = 255, message = "{session.wifi.password.length.invalid}")
    @Pattern(regexp = Constants.WIFI_PASSWORD_PATTERN, message = "session.wifi.password.invalid")
    private String wifiPassword;

    //For unlimited data put 200 GB, else put minimum 100
    @Range(min = Constants.MINIMUM_SELLING_DATA_MB, max = Constants.MAXIMUM_SELLING_DATA_MB, message = "{session.data.invalid}")
    private int data;

    //Unit Price, i.e. Price per GB
    @Range(min = Constants.MINIMUM_SELLING_DATA_PRICE_PER_GB, max = Constants.MAXIMUM_SELLING_DATA_PRICE_PER_GB, message = "{session.price.invalid}")
    private double price;

    private boolean isWifi;

}
