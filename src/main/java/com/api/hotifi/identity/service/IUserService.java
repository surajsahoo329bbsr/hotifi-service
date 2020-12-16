package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.web.request.UserRequest;

public interface IUserService {

    void addUser(UserRequest userRequest);

    User getUserByUsername(String username);

    void generateEmailOtpLogin(Long id);

    void verifyEmailOtp(Long id, String emailOtp);

    void updateUser(UserRequest userRequest);

    void updateLoginStatus(Long id, boolean loginStatus);

}
