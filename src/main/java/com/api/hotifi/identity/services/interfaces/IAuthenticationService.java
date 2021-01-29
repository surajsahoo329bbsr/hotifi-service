package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IAuthenticationService extends UserDetailsService {

    String addEmail(String email, boolean isEmailVerified);

    Authentication getAuthentication(String email);

    void resendEmailOtpSignUp(String email);

    void verifyEmail(String email, String otp);

    void verifyPhone(String email, String countryCode, String phone);

}
