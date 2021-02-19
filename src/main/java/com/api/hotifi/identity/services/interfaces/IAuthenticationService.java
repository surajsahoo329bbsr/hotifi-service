package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IAuthenticationService extends UserDetailsService {

    String addEmail(String email, String identifier, String token, String socialClient);

    Authentication getAuthenticationForAdminstrators(String email);

    Authentication getAuthenticationForCustomer(String email);

    void resendEmailOtpSignUp(String email);

    void verifyEmail(String email, String otp);

    void verifyPhone(String email, String countryCode, String phone, String idToken);

}
