package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.web.response.CredentialsResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IAuthenticationService extends UserDetailsService {

    CredentialsResponse addEmail(String email, String identifier, String token, String socialClient);

    Authentication getAuthentication(String email, boolean isAdmin);

    void resendEmailOtpSignUp(String email);

    void verifyEmail(String email, String otp);

    void verifyPhone(String email, String countryCode, String phone);

    boolean isPhoneAvailable(String phone);

    boolean isEmailAvailable(String email);

}
