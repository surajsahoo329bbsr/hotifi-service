package com.api.hotifi.identity.web.request;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
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

    @Range(min = 1, message = "{purchase.id.invalid}")
    private Long purchaseId;

    @NotBlank(message = "{role.name.blank}")
    @Pattern(regexp = BusinessConfigurations.VALID_ROLE_PATTERN, message = "{role.name.invalid}")
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
