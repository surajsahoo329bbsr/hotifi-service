package com.api.hotifi.identity.web.request;

import com.api.hotifi.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserStatusRequest {

    @Digits(integer = 18, fraction = 0, message = "{user.id.invalid}")
    private Long userId;

    @NotBlank
    @Pattern(regexp = Constants.VALID_ROLE_PATTERN, message = "{role.name.invalid}")
    private String role;

    @Length(max = 255, message = "{warning.reason.invalid}")
    private String warningReason;

    @Length(max = 255, message = "{freeze.reason.invalid}")
    private String freezeReason;

    @Length(max = 255, message = "{ban.reason.invalid}")
    private String banReason;

    @Length(max = 255, message = "{delete.reason.invalid}")
    private String deleteReason;

}
