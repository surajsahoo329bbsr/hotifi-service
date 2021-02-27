package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.services.interfaces.IEmailService;
import com.api.hotifi.common.utils.AESUtils;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.AuthenticationErrorCodes;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.models.EmailModel;
import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.services.interfaces.IUserService;
import com.api.hotifi.identity.utils.OtpUtils;
import com.api.hotifi.identity.web.request.UserRequest;
import com.api.hotifi.identity.web.response.CredentialsResponse;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final AuthenticationRepository authenticationRepository;
    private final IEmailService emailService;

    public UserServiceImpl(UserRepository userRepository, AuthenticationRepository authenticationRepository, IEmailService emailService) {
        this.userRepository = userRepository;
        this.authenticationRepository = authenticationRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void addUser(UserRequest userRequest) {
        Authentication authentication = authenticationRepository.findById(userRequest.getAuthenticationId()).orElse(null);
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        if (!authentication.isEmailVerified() || !authentication.isPhoneVerified())
            throw new HotifiException(AuthenticationErrorCodes.AUTHENTICATION_NOT_VERIFIED);
        if (userRepository.existsByFacebookId(userRequest.getFacebookId()) && userRequest.getFacebookId() != null)
            throw new HotifiException(UserErrorCodes.FACEBOOK_USER_EXISTS);
        if (userRepository.existsByGoogleId(userRequest.getGoogleId()) && userRequest.getGoogleId() != null)
            throw new HotifiException(UserErrorCodes.GOOGLE_USER_EXISTS);
        if (userRepository.existsByUsername(userRequest.getUsername()))
            throw new HotifiException(UserErrorCodes.USERNAME_EXISTS);
        try {
            User user = new User();
            authentication.setActivated(true);
            authentication.setEmailOtp(null);
            setUser(userRequest, user, authentication);
            userRepository.save(user);
            authenticationRepository.save(authentication);

            EmailModel emailModel = new EmailModel();
            emailModel.setToEmail(authentication.getEmail());
            emailModel.setFromEmail(Constants.FROM_EMAIL);
            emailModel.setFromEmailPassword(Constants.FROM_EMAIL_PASSWORD);
            emailService.sendWelcomeEmail(user, emailModel);
        } catch (DataIntegrityViolationException e) {
            throw new HotifiException(UserErrorCodes.USER_EXISTS);
        } catch (Exception e) {
            log.error("Error ", e);
            throw new HotifiException(UserErrorCodes.UNEXPECTED_USER_ERROR);
        }
    }

    //To check if username is available in database
    @Override
    @Transactional
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    @Override
    public void sendEmailOtpLogin(String email) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        User user = authentication != null ? userRepository.findByAuthenticationId(authentication.getId()) : null;
        //If user doesn't exist no need to check legit authentication
        if (user == null)
            throw new HotifiException(UserErrorCodes.USER_EXISTS);
        if (authentication.getEmailOtp() != null)
            throw new HotifiException(UserErrorCodes.EMAIL_OTP_ALREADY_GENERATED);
        if (!LegitUtils.isAuthenticationLegit(authentication))
            throw new HotifiException(AuthenticationErrorCodes.AUTHENTICATION_NOT_LEGIT);

        OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Transactional
    @Override
    public void resendEmailOtpLogin(String email) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        User user = authentication != null ? userRepository.findByAuthenticationId(authentication.getId()) : null;
        if (user == null)
            throw new HotifiException(UserErrorCodes.USER_EXISTS);
        if (!LegitUtils.isAuthenticationLegit(authentication))
            throw new HotifiException(AuthenticationErrorCodes.AUTHENTICATION_NOT_LEGIT);

        OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService);
    }

    //DO NOT ADD @Transaction
    @Override
    public CredentialsResponse verifyEmailOtp(String email, String emailOtp) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (OtpUtils.isEmailOtpExpired(authentication)) {
            log.error("Otp Expired");
            authentication.setEmailOtp(null);
            authenticationRepository.save(authentication);
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_EXPIRED);
        }

        String encryptedEmailOtp = authentication.getEmailOtp();
        if (BCrypt.checkpw(emailOtp, encryptedEmailOtp)) {
            authentication.setEmailOtp(null);
            authentication.setEmailVerified(true);
            authenticationRepository.save(authentication);
            log.info("User Email Verified");
        } else
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_INVALID);

        String decryptedPassword = AESUtils.decrypt(authentication.getPassword(), Constants.AES_PASSWORD_SECRET_KEY);
        return new CredentialsResponse(authentication.getEmail(), decryptedPassword);
    }

    @Override
    @Transactional
    public void updateUserLogin(String email, boolean isLogin){
        Authentication authentication = authenticationRepository.findByEmail(email);
        User user = authentication != null ? userRepository.findByAuthenticationId(authentication.getId()) : null;
        if(LegitUtils.isUserLegit(user)) {
            Date logTime = new Date(System.currentTimeMillis());
            user.setLoggedIn(isLogin);
            user.setLoggedAt(logTime);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void updateUser(UserRequest userUpdateRequest) {
        Long authenticationId = userUpdateRequest.getAuthenticationId();
        User user = userRepository.findByAuthenticationId(authenticationId);
        if (!LegitUtils.isUserLegit(user) && !user.isLoggedIn())
            throw new HotifiException(UserErrorCodes.USER_NOT_LEGIT);
        Authentication authentication = user.getAuthentication();
        setUser(userUpdateRequest, user, authentication);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateLoginStatus(Long id, boolean loginStatus) {
        User user = userRepository.findById(id).orElse(null);
        if (!LegitUtils.isUserLegit(user))
            throw new HotifiException(UserErrorCodes.USER_NOT_LEGIT);
        if (user.isLoggedIn() && loginStatus)
            throw new HotifiException(UserErrorCodes.USER_ALREADY_LOGGED_IN);
        else if (!user.isLoggedIn() && !loginStatus)
            throw new HotifiException(UserErrorCodes.USER_ALREADY_LOGGED_OUT);
        else {
            user.setLoggedIn(loginStatus);
            userRepository.save(user);
        }
    }

    //user defined functions
    //setting up user's values
    public void setUser(UserRequest userRequest, User user, Authentication authentication) {
        user.setAuthentication(authentication);
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setFacebookId(userRequest.getFacebookId());
        user.setGoogleId(userRequest.getGoogleId());
        user.setUsername(userRequest.getUsername());
        user.setPhotoUrl(userRequest.getPhotoUrl());
        user.setDateOfBirth(userRequest.getDateOfBirth());
    }

}