package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.Authentication;

public interface IAuthenticationService {

    void addEmail(String email, boolean isEmailVerified);

    Authentication getAuthentication(String email);

    void generateEmailOtpSignUp(String email);

    void verifyEmailOtp(String email, String otp);

    void verifyPhone(String email, String countryCode, String phone);

}
