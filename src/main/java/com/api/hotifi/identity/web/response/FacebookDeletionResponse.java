package com.api.hotifi.identity.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public class FacebookDeletionResponse implements Serializable {

    @NotBlank(message = "{facebook.status.url.blank}")
    private String url;

    @NotBlank(message = "{facebook.confirmation.code.blank}") //alphanumeric - no symbols (within 6-20 characters)
    private String confirmation_code; //do not change to camel case, it's required by facebook only

}
