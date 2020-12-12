package com.api.hotifi.identity.web.request;

import com.api.hotifi.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Getter
@Setter
public class UserRequest {

    @NotBlank(message = "Please provide first name.")
    private String firstName;

    @NotBlank(message = "Please provide last name.")
    private String lastName;

    @NotBlank(message = "Please provide a date of birth")
    @Pattern(regexp = Constants.VALID_USERNAME_PATTERN, message = "{user.username.invalid}")
    private String username;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @NotNull(message = "Please provide a date.")
    private Date dateOfBirth;

    private String facebookId;

    private String googleId;

    private String photoUrl;

    @Digits(integer = 18, fraction = 0)
    private long authenticationId;

}
