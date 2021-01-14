package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.errors.AuthenticationErrorCodes;
import com.api.hotifi.identity.errors.UserErrorMessages;
import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.services.interfaces.IEmailService;
import com.api.hotifi.identity.utils.OtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private IEmailService emailService;

    @Transactional
    @Override
    public Authentication getAuthentication(String email) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        return authentication;
    }

    @Transactional
    @Override
    //If login client already has email verified no need for further verification
    public String addEmail(String email, boolean isEmailVerified) {
        try {
            Authentication authentication = new Authentication();
            authentication.setEmail(email);
            authentication.setEmailVerified(isEmailVerified);

            if (!isEmailVerified)
                return OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService);

            String token = UUID.randomUUID().toString();
            log.info("Token" + token);
            String encryptedToken = BCrypt.hashpw(token, BCrypt.gensalt());
            authentication.setToken(encryptedToken);
            authenticationRepository.save(authentication);
            return token;

        } catch (DataIntegrityViolationException e) {
            log.error(UserErrorMessages.USER_EXISTS);
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_ALREADY_EXISTS);
        } catch (Exception e) {
            log.error("Error Occurred ", e);
            throw new HotifiException(AuthenticationErrorCodes.UNEXPECTED_AUTHENTICATION_ERROR);
        }

    }


    @Transactional
    @Override
    public String regenerateEmailOtpSignUp(String email) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        //Since it is signup so no need for verifying legit user
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        if (authentication.isEmailVerified())
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_ALREADY_VERIFIED);

        //If token created at is null, it means otp is generated for first time or Otp duration expired and we are setting new Otp
        log.info("Regenerating Otp...");
        return Objects.requireNonNull(OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService));

    }

    //@Transaction cannot be added here
    @Override
    public void verifyEmailOtp(String email, String emailOtp) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        String encryptedEmailOtp = authentication.getEmailOtp();
        if (authentication.isEmailVerified())
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_ALREADY_VERIFIED);

        if (OtpUtils.isEmailOtpExpired(authentication)) {
            log.error("Otp Expired");
            authentication.setEmailOtp(null);
            authenticationRepository.save(authentication);
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_EXPIRED);
        }

        if (BCrypt.checkpw(emailOtp, encryptedEmailOtp)) {
            authentication.setEmailOtp(null);
            authentication.setEmailVerified(true);
            authenticationRepository.save(authentication);
            log.info("User Email Verified");
        } else {
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_INVALID);
        }
    }

    @Transactional
    @Override
    public void verifyPhone(String email, String countryCode, String phone) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        if (!authentication.isEmailVerified())
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_NOT_VERIFIED);
        if (authentication.isPhoneVerified())
            throw new HotifiException(AuthenticationErrorCodes.PHONE_ALREADY_VERIFIED);
        try {
            authentication.setCountryCode(countryCode);
            authentication.setPhone(phone);
            authentication.setPhoneVerified(true);
            authenticationRepository.save(authentication);
        } catch (DataIntegrityViolationException e) {
            throw new HotifiException(AuthenticationErrorCodes.PHONE_ALREADY_EXISTS);
        } catch (Exception e) {
            throw new HotifiException(AuthenticationErrorCodes.UNEXPECTED_AUTHENTICATION_ERROR);
        }
    }
}
