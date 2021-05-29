package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.processors.codes.SocialCodes;
import com.api.hotifi.common.services.interfaces.IEmailService;
import com.api.hotifi.common.services.interfaces.IVerificationService;
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
import java.util.UUID;

@Slf4j
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final AuthenticationRepository authenticationRepository;
    private final IEmailService emailService;
    private final IVerificationService verificationService;

    public UserServiceImpl(UserRepository userRepository, AuthenticationRepository authenticationRepository, IEmailService emailService, IVerificationService verificationService) {
        this.userRepository = userRepository;
        this.authenticationRepository = authenticationRepository;
        this.emailService = emailService;
        this.verificationService = verificationService;
    }

    @Override
    @Transactional
    public void addUser(UserRequest userRequest) {
        Authentication authentication = authenticationRepository.findById(userRequest.getAuthenticationId()).orElse(null);
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_NOT_FOUND);
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
            emailModel.setFromEmail(AppConfigurations.FROM_EMAIL);
            emailModel.setFromEmailPassword(AppConfigurations.FROM_EMAIL_PASSWORD);
            emailService.sendWelcomeEmail(user, emailModel);
        } catch (DataIntegrityViolationException e) {
            throw new HotifiException(UserErrorCodes.USER_EXISTS);
        } catch (Exception e) {
            log.error("Error ", e);
            throw new HotifiException(UserErrorCodes.UNEXPECTED_USER_ERROR);
        }
    }

    @Transactional
    @Override
    public CredentialsResponse resetPassword(String email, String emailOtp, String identifier, String token, SocialCodes socialCode) {
        boolean isSocialLogin = identifier != null && token != null && socialCode != null;
        boolean isCustomLogin = emailOtp != null;
        boolean isBothSocialCustomLogin = isCustomLogin == isSocialLogin;
        if (isBothSocialCustomLogin)
            throw new HotifiException(UserErrorCodes.BAD_RESET_PASSWORD_REQUEST);
        Authentication authentication = authenticationRepository.findByEmail(email);
        User user = authentication != null ? userRepository.findByAuthenticationId(authentication.getId()) : null;
        if (user == null)
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        if (!LegitUtils.isAuthenticationLegit(authentication))
            throw new HotifiException(AuthenticationErrorCodes.AUTHENTICATION_NOT_LEGIT);

        boolean isSocialUserVerified = isSocialLogin && verificationService.isSocialUserVerified(email, identifier, token, socialCode);

        if (!isSocialUserVerified && OtpUtils.isEmailOtpExpired(authentication)) {
            log.error("Otp Expired");
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_EXPIRED);
        }
        if (isSocialUserVerified || BCrypt.checkpw(emailOtp, authentication.getEmailOtp())) {
            log.info("User Email Verified");
            String newPassword = UUID.randomUUID().toString();
            String encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            log.info("new : " + newPassword);
            log.info("enc : " + encryptedPassword);
            authentication.setPassword(encryptedPassword);
            authenticationRepository.save(authentication);
            return new CredentialsResponse(email, newPassword);
        }

        throw new HotifiException(UserErrorCodes.UNEXPECTED_USER_ERROR);
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
        //If user doesn't exist no need to check legit authentication
        if (authentication.getEmailOtp() != null && !OtpUtils.isEmailOtpExpired(authentication))
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

    //DO NOT ADD @Transactional
    @Override
    public void verifyEmailOtpLogin(String email, String emailOtp) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (OtpUtils.isEmailOtpExpired(authentication)) {
            log.error("Otp Expired");
            authentication.setEmailOtp(null);
            authenticationRepository.save(authentication);
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_EXPIRED);
        }

        String encryptedEmailOtp = authentication.getEmailOtp();
        String password = UUID.randomUUID().toString();
        String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        if (BCrypt.checkpw(emailOtp, encryptedEmailOtp)) {
            authentication.setEmailOtp(null);
            authentication.setEmailVerified(true);
            authentication.setPassword(encryptedPassword);
            authenticationRepository.save(authentication);
            log.info("User Email Verified");
        } else
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_INVALID);
    }

    @Override
    @Transactional
    public void updateUserLogin(String email, boolean isLogin) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        User user = authentication != null ? userRepository.findByAuthenticationId(authentication.getId()) : null;
        if (!LegitUtils.isUserLegit(user))
            throw new HotifiException(UserErrorCodes.USER_NOT_LEGIT);
        Date logTime = new Date(System.currentTimeMillis());
        user.setLoggedIn(isLogin);
        user.setLoggedAt(logTime);
        userRepository.save(user);
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
    public User getUserByEmail(String email) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        Long authenticationId = (authentication != null) ? authentication.getId() : null;
        User user = userRepository.findByAuthenticationId(authenticationId);
        if (!LegitUtils.isUserLegit(user))
            throw new HotifiException(UserErrorCodes.USER_NOT_LEGIT);
        return user;
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