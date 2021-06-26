package com.api.hotifi.common.web.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class NotificationCommonRequest {

    @NotBlank(message = "{title.blank}")
    @Length(max = 255, message = "{title.length.invalid}")
    private String title;

    @Length(max = 255, message = "{photo.url.length.invalid}")
    private String photoUrl;

    @NotBlank(message = "{message.blank}")
    @Length(max = 255, message = "{message.length.invalid}")
    private String message;

}
