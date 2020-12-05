package com.api.hotifi.identity.web.request;

import com.api.hotifi.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PhoneRequest {

    @NotBlank(message = "{email.empty}")
    @Email(message = "{invalid.email}")
    @Length(max = 255, message = "{email.invalid.length}")
    private String email;

    @NotBlank(message = "{phone.cc.empty}")
    //@Pattern(regexp = Constants.VALID_COUNTRY_CODE_PATTERN, message = "{phone.cc.invalid}")
    private String countryCode;

    @NotBlank(message = "{phone.number.empty}")
    @Pattern(regexp = Constants.VALID_PHONE_PATTERN, message = "{phone.number.invalid}")
    private String phone;

}
