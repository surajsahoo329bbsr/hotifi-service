package com.api.hotifi.identity.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class PasswordTokenResponse {

    @NotBlank
    @Length(max = 255, message = "{password.token.length.invalid}")
    private String passwordToken;

}
