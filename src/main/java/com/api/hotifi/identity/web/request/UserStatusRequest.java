package com.api.hotifi.identity.web.request;

import com.api.hotifi.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserStatusRequest {

    @Range(min = 1, message = "{user.id.invalid}")
    private Long userId;

    @NotBlank(message = "{role.name.blank}")
    @Pattern(regexp = Constants.VALID_ROLE_PATTERN, message = "{role.name.invalid}")
    private String role;

    @NotBlank(message = "{warning.reason.blank}")
    @Length(max = 255, message = "{warning.reason.invalid}")
    private String warningReason;

    @NotBlank(message = "freeze.reason.blank")
    @Length(max = 255, message = "{freeze.reason.invalid}")
    private String freezeReason;

    @NotBlank(message = "{ban.reason.blank}")
    @Length(max = 255, message = "{ban.reason.invalid}")
    private String banReason;

    @NotBlank(message = "{delete.reason.blank}")
    @Length(max = 255, message = "{delete.reason.invalid}")
    private String deleteReason;

}
