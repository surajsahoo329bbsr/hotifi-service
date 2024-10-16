package com.api.hotifi.session.web.request;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

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
    @Pattern(regexp = BusinessConfigurations.WIFI_PASSWORD_PATTERN, message = "session.wifi.password.invalid")
    private String wifiPassword;

    //For unlimited data put 200 GB, else put minimum 100
    @Range(min = BusinessConfigurations.MINIMUM_SELLING_DATA_MB, max = BusinessConfigurations.MAXIMUM_SELLING_DATA_MB, message = "{session.data.invalid}")
    private int data;

    //Unit Price, i.e. Price per GB
    @Range(min = BusinessConfigurations.MINIMUM_SELLING_DATA_PRICE_PER_GB, max = BusinessConfigurations.MAXIMUM_SELLING_DATA_PRICE_PER_GB, message = "{session.price.invalid}")
    private BigDecimal price;

    private boolean isWifi;

}
