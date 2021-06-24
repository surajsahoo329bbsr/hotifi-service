package com.api.hotifi.common.web.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class NotificationRequest {

    @Range(min = 1, message = "{user.id.invalid}")
    private Long userId;

    @NotBlank(message = "{title.blank}")
    @Length(max = 255, message = "{title.length.invalid}")
    private String title;

    @NotBlank(message = "{message.blank}")
    @Length(max = 255, message = "{message.length.invalid}")
    private String message;
}
