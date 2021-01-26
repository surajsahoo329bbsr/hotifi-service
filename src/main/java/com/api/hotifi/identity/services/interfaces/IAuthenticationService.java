package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IAuthenticationService extends UserDetailsService {

    void addEmail(String email, boolean isEmailVerified);

    Authentication getAuthentication(String email);

    void regenerateEmailOtpSignUp(String email);

    void verifyEmailOtp(String email, String otp);

    void verifyPhone(String email, String countryCode, String phone);

}
