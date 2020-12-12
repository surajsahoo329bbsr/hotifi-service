package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.Authentication;

public interface IAuthenticationService {

    void addEmail(String email, boolean isEmailVerified);

    Authentication getAuthentication(String email);

    void generateEmailOtpLogin(String email);

    void verifyEmailOtp(String email, String otp);

    void verifyPhoneUser(String email, String countryCode, String phone);

    void activateUser(String email, boolean activateUser);

    void banUser(String email, boolean banUser);

    void freezeUser(String email, boolean freezeUser);

    void deleteUser(String email, boolean deleteUser);

}
