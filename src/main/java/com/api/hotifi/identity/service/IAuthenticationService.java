package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.Authentication;

public interface IAuthenticationService {

    void addEmail(String email, boolean isEmailVerified);

    boolean isEmailAvailable(String email);

    String generateEmailOtp(Authentication authentication);

    boolean verifyEmailOtp(String email, String otp);

    void activateUserEmail(String email, boolean activateUserEmail);

    void activateUser(String email, boolean activateUser);

    void banUser(String email, boolean banUser);

    void freezeUser(String email, boolean freezeUser);

    void deleteUser(String email, boolean deleteUser);

}
