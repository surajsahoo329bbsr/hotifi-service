package com.api.hotifi.identity.web.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EmailRequest {

    @NotBlank(message = "{email.empty}")
    @Email(message = "{invalid.email}")
    @Length(max = 255, message = "{email.invalid.length}")
    private String email;

}
