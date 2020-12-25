package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.Authentication;

public interface IAuthenticationService {

    void addEmail(String email, boolean isEmailVerified);

    Authentication getAuthentication(String email);

    void generateEmailOtpSignUp(String email);

    void verifyEmailOtp(String email, String otp);

    void verifyPhone(String email, String countryCode, String phone);

}
