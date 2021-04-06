package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.common.processors.codes.SocialCodes;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.web.request.UserRequest;
import com.api.hotifi.identity.web.response.CredentialsResponse;

public interface IUserService {

    void addUser(UserRequest userRequest);

    CredentialsResponse resetPassword(String email, String emailOtp, String identifier, String token, SocialCodes socialCode);

    User getUserByUsername(String username);

    void sendEmailOtpLogin(String email);

    boolean isUsernameAvailable(String username);

    void resendEmailOtpLogin(String email);

    void verifyEmailOtpLogin(String email, String emailOtp);

    void updateUser(UserRequest userRequest);

    void updateUserLogin(String email, boolean isLogin);

    User getUserByEmail(String email);

}
