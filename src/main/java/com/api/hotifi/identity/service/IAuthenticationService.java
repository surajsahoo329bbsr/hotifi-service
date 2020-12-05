package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.Authentication;

public interface IAuthenticationService {

    void addEmail(String email, boolean isEmailVerified);

    Authentication getAuthentication(String email);

    void generateEmailOtpSignUp(Authentication authentication);

    String generateEmailOtpLogin(String email);

    boolean verifyEmailOtp(String email, String otp);

    boolean verifyPhoneUser(String email, String countryCode, String phone);

    void activateUserEmail(String email, boolean activateUserEmail);

    void activateUser(String email, boolean activateUser);

    void banUser(String email, boolean banUser);

    void freezeUser(String email, boolean freezeUser);

    void deleteUser(String email, boolean deleteUser);

}
