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
    @NotBlank(message = "{email.empty}")
    @Email(message = "{invalid.email}")
    @Length(max = 255, message = "{email.invalid.length}")
    private String email;

    @NotBlank(message = "{otp.empty}")
    @Pattern(regexp = Constants.VALID_OTP_PATTERN, message = "{otp.invalid.number}")
    @Length(min = 6, max = 6, message = "{email.invalid.length")
    private String otp;
}
