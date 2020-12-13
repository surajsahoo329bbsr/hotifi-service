package com.api.hotifi.identity.web.request;

import com.api.hotifi.common.constant.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
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

    @ApiModelProperty(required = true,example = "02-01-1998")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull(message = "Please provide a date")
    private Date dateOfBirth;

    private String facebookId;

    private String googleId;

    private String photoUrl;

    @Min(1)
    private Long authenticationId;

}
