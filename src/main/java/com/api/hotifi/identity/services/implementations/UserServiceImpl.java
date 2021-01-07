package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.AuthenticationErrorCodes;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.model.EmailModel;
import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.services.interfaces.IEmailService;
import com.api.hotifi.identity.services.interfaces.IUserService;
import com.api.hotifi.identity.utils.OtpUtils;
import com.api.hotifi.identity.web.request.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private IEmailService emailService;

    @Override
    @Transactional
    public void addUser(UserRequest userRequest) {
        Authentication authentication = authenticationRepository.findById(userRequest.getAuthenticationId()).orElse(null);
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        if (!authentication.isEmailVerified() || !authentication.isPhoneVerified())
            throw new HotifiException(AuthenticationErrorCodes.AUTHENTICATION_NOT_VERIFIED);

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
            emailService.sendEmail(user, emailModel, 1);
        } catch (DataIntegrityViolationException e) {
            log.error("Not Authenticated", e);
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
    public String generateEmailOtpLogin(String email) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        User user = authentication != null ? userRepository.findByAuthenticationId(authentication.getId()) : null;
        //If user doesn't exist no need to check legit authentication
        if (user == null)
            throw new HotifiException(UserErrorCodes.USER_EXISTS);
        if (authentication.getEmailOtp() != null)
            throw new HotifiException(UserErrorCodes.EMAIL_OTP_ALREADY_GENERATED);
        if (!LegitUtils.isAuthenticationLegit(authentication))
            throw new HotifiException(AuthenticationErrorCodes.AUTHENTICATION_NOT_LEGIT);

        return OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService);
    }

    @Transactional
    @Override
    public String regenerateEmailOtpLogin(String email) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        User user = authentication != null ? userRepository.findByAuthenticationId(authentication.getId()) : null;
        if (user == null)
            throw new HotifiException(UserErrorCodes.USER_EXISTS);
        if (!LegitUtils.isAuthenticationLegit(authentication))
            throw new HotifiException(AuthenticationErrorCodes.AUTHENTICATION_NOT_LEGIT);

        return OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService);
    }

    //DO NOT ADD @Transaction
    @Override
    public void verifyEmailOtp(String email, String emailOtp) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (OtpUtils.isEmailOtpExpired(authentication)) {
            log.error("Otp Expired");
            authentication.setEmailOtp(null);
            authenticationRepository.save(authentication);
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_EXPIRED);
        }

        User user = userRepository.findByAuthenticationId(authentication.getId());
        String encryptedEmailOtp = authentication.getEmailOtp();
        if (BCrypt.checkpw(emailOtp, encryptedEmailOtp)) {
            authentication.setEmailOtp(null);
            authentication.setEmailVerified(true);
            authenticationRepository.save(authentication);
            //after successful otp verification log in
            user.setLoggedIn(true);
            userRepository.save(user);
            log.info("User Email Verified");
        } else
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_INVALID);
    }

    @Override
    @Transactional
    public void updateUser(UserRequest userUpdateRequest) {
        Long authenticationId = userUpdateRequest.getAuthenticationId();
        User user = userRepository.findByAuthenticationId(authenticationId);
        if (!LegitUtils.isUserLegit(user) && user.isLoggedIn())
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

    @Transactional
    @Override
    public void updateUpiId(Long id, String upiId) {

            User user = userRepository.findById(id).orElse(null);
            if (!LegitUtils.isUserLegit(user))
                throw new HotifiException(UserErrorCodes.USER_NOT_LEGIT);
        try {
            //TODO Upi Id Check From Razor Pay
            user.setUpiId(upiId);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(UserErrorCodes.UNEXPECTED_USER_ERROR);
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