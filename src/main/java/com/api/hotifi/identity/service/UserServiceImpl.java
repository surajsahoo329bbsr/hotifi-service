package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.Authentication;
import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.error.UserErrorMessages;
import com.api.hotifi.identity.repository.AuthenticationRepository;
import com.api.hotifi.identity.repository.UserRepository;
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

    @Override
    public void generateEmailOtpLogin(Long id) {
        try {
            User user = userRepository.getOne(id);
            Long authenticationId = user.getAuthentication().getId();
            Authentication authentication = authenticationRepository.getOne(authenticationId);

            if (authentication.getEmailOtp() != null)
                throw new Exception(UserErrorMessages.OTP_ALREADY_GENERATED);
            //Here full check is done because email/phone verification can be false while updating email/phone
            boolean isAuthLegit = authentication.isEmailVerified() && authentication.isPhoneVerified()
                    && authentication.isActivated() && !authentication.isFreezed()
                    && !authentication.isBanned() && !authentication.isDeleted();
            if (!isAuthLegit)
                throw new Exception("Authentication is not Legit");

            OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository);

        } catch (Exception e) {
            log.error("Error Occured ", e);
        }
    }

    @Override
    public void verifyEmailOtp(Long id, String emailOtp) {
        try {
            User user = userRepository.getOne(id);
            Long authenticationId = user.getAuthentication().getId();
            Authentication authentication = authenticationRepository.getOne(authenticationId);
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
            if (!isUserLegit(user))
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
            if (!isUserLegit(user))
                throw new Exception("User not legit to be updated");
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

    //returns true if user is legit
    //Not checking for verified user since while adding user task has already been completed
    //because user would not be created if email and phone have not been verified
    public boolean isUserLegit(User user) {
        if (user == null)
            return false;
        //login check not required because if user has been created then phone and email has been already verified
        return !user.getAuthentication().isDeleted() && user.getAuthentication().isActivated() && !user.getAuthentication().isBanned() && !user.getAuthentication().isFreezed();
    }
}