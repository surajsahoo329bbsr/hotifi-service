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
public class EmailOtpRequest {

    @NotBlank(message = "{email.blank}")
    @Email(message = "{email.pattern.invalid}")
    @Length(max = 255, message = "{email.length.invalid}")
    private String email;

    @NotBlank(message = "{otp.blank}")
    @Pattern(regexp = Constants.VALID_OTP_PATTERN, message = "{otp.number.invalid}")
    private String otp;

}
