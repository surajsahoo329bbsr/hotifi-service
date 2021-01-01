package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.UserErrorMessages;
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
        try {
            Authentication authentication = authenticationRepository.getOne(userRequest.getAuthenticationId());
            if (!authentication.isEmailVerified() || !authentication.isPhoneVerified()) {
                log.error("User not verified log");
                throw new Exception("User not verified");
            }
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
            throw new DataIntegrityViolationException("Not Authenticted", e);
        } catch (Exception e) {
            log.error("Error ", e);
        }
    }

    //To check if username is available in database
    @Override
    @Transactional
    public User getUserByUsername(String username) {
        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            log.error("Error ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public String generateEmailOtpLogin(String email) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            User user = userRepository.findByAuthenticationId(authentication.getId());
            if(user == null)
                throw new Exception("User not found");
            if (authentication.getEmailOtp() != null)
                throw new Exception(UserErrorMessages.OTP_ALREADY_GENERATED);
            //If user doesn't exist no need to check legit authentication
            if (!LegitUtils.isAuthenticationLegit(authentication))
                throw new Exception("Authentication is not Legit");

            return OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService);

        } catch (Exception e) {
            log.error("Error Occured ", e);
        }

        return  null;
    }

    @Transactional
    @Override
    public String regenerateEmailOtpLogin(String email){
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            User user = userRepository.findByAuthenticationId(authentication.getId());
            if(user == null)
                throw new Exception("User not found");
            //If user doesn't exist no need to check legit authentication
            if (!LegitUtils.isAuthenticationLegit(authentication))
                throw new Exception("Authentication is not Legit");

            return OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService);

        } catch (Exception e) {
            log.error("Error Occured ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public void verifyEmailOtp(String email, String emailOtp) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            User user = userRepository.findByAuthenticationId(authentication.getId());
            String encryptedEmailOtp = authentication.getEmailOtp();

            if (OtpUtils.isEmailOtpExpired(authentication)) {
                log.error("Otp Expired");
                authentication.setEmailOtp(null);
                authenticationRepository.save(authentication);
                return;
            }

            if (BCrypt.checkpw(emailOtp, encryptedEmailOtp)) {
                authentication.setEmailOtp(null);
                authentication.setEmailVerified(true);
                authenticationRepository.save(authentication);
                //after successful otp verification log in
                user.setLoggedIn(true);
                userRepository.save(user);
                log.info("User Email Verified");
            }
        } catch (Exception e) {
            log.error("Error Occured" + e);
        }
    }

    @Override
    @Transactional
    public void updateUser(UserRequest userUpdateRequest) {
        try {
            Long authenticationId = userUpdateRequest.getAuthenticationId();
            User user = userRepository.findByAuthenticationId(authenticationId);
            if (!LegitUtils.isUserLegit(user) && user.isLoggedIn())
                throw new Exception("User not legit to be updated");
            Authentication authentication = user.getAuthentication();
            setUser(userUpdateRequest, user, authentication);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    @Transactional
    @Override
    public void updateLoginStatus(Long id, boolean loginStatus) {
        try {
            User user = userRepository.getOne(id);
            if (!LegitUtils.isUserLegit(user))
                throw new Exception("User not legit to be logged in/out");
            if (user.isLoggedIn() && loginStatus) {
                log.error("Already logged in");
                throw new Exception("Already logged in");
            } else if (!user.isLoggedIn() && !loginStatus) {
                log.error("Already logged out");
                throw new Exception("Already logged out");
            } else {
                user.setLoggedIn(loginStatus);
                userRepository.save(user);
            }
        } catch (Exception e) {
            log.error("Error ", e);
        }
    }

    @Transactional
    @Override
    public void updateUpiId(Long id, String upiId) {
        try{
            User user = userRepository.findById(id).orElse(null);
            if(!LegitUtils.isUserLegit(user))
                throw new Exception("User not legit to be updated");
            //TODO Upi Id Check From Razor Pay
            user.setUpiId(upiId);
            userRepository.save(user);
        }
        catch (Exception e){
            log.error("Error occurred ", e);
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