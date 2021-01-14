package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.Authentication;

public interface IAuthenticationService {

    String addEmail(String email, boolean isEmailVerified);

    Authentication getAuthentication(String email);

    String regenerateEmailOtpSignUp(String email);

    void verifyEmailOtp(String email, String otp);

    void verifyPhone(String email, String countryCode, String phone);

}
