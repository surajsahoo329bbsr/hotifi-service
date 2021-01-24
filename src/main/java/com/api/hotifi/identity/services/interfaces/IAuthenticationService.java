package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public interface IAuthenticationService extends UserDetailsService {

    String addEmail(String email, boolean isEmailVerified);

    Authentication getAuthentication(String email);

    OAuth2AccessToken getAccessToken(String email, String clientId, String token);

    String regenerateEmailOtpSignUp(String email);

    void verifyEmailOtp(String email, String otp);

    void verifyPhone(String email, String countryCode, String phone);

}
