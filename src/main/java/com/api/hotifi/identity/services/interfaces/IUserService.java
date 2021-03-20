package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.web.request.UserRequest;
import com.api.hotifi.identity.web.response.CredentialsResponse;

public interface IUserService {

    void addUser(UserRequest userRequest);

    User getUserByUsername(String username);

    void sendEmailOtpLogin(String email);

    boolean isUsernameAvailable(String username);

    boolean isPhoneAvailable(String username);

    void resendEmailOtpLogin(String email);

    CredentialsResponse verifyEmailOtp(String email, String emailOtp);

    void updateUser(UserRequest userRequest);

    void updateUserLogin(String email, boolean isLogin);

    void updateLoginStatus(Long id, boolean loginStatus);

}
