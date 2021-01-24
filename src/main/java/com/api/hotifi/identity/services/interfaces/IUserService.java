package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.web.request.UserRequest;

public interface IUserService {

    void addUser(UserRequest userRequest);

    User getUserByUsername(String username);

    String generateEmailOtpLogin(String email);

    boolean isUsernameAvailable(String username);

    String regenerateEmailOtpLogin(String email);

    void verifyEmailOtpAndLogin(String email, String emailOtp);

    void updateUser(UserRequest userRequest);

    void updateLoginStatus(Long id, boolean loginStatus);

}
