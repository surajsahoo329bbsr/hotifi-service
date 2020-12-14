package com.api.hotifi.identity.service;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.exception.error.ErrorCode;
import com.api.hotifi.identity.entity.Authentication;
import com.api.hotifi.identity.error.UserErrorMessages;
import com.api.hotifi.identity.repository.AuthenticationRepository;
import com.api.hotifi.identity.utils.OtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    //Implements
    @Transactional
    @Override
    //If login client already has email verified no need for further verification
    public void addEmail(String email, boolean isEmailVerified) {
        try {
            Authentication authentication = new Authentication();
            authentication.setEmail(email);
            authentication.setEmailVerified(isEmailVerified);

            if (!isEmailVerified)
                OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository);
            else{
                String token = UUID.randomUUID().toString();
                log.info("Token"+ token);
                String encryptedToken = BCrypt.hashpw(token, BCrypt.gensalt());
                authentication.setToken(encryptedToken);
                authenticationRepository.save(authentication);
            }

        } catch (DataIntegrityViolationException e) {
            log.error(UserErrorMessages.USER_EXISTS);
        } catch (Exception e) {
            log.error("Error Occurred ", e);
        }
    }

    @Transactional
    @Override
    public Authentication getAuthentication(String email) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            if (authentication == null) {
                throw new HotifiException("Email doesn't exist", new ErrorCode("Email doesn't exist", null, 500));
            }
            return authentication;
        } catch (Exception e) {
            log.error("Error occured ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public void generateEmailOtpSignUp(String email) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            //Since it is signup so no need for verifying legit user
            if(authentication.isEmailVerified())
                throw new Exception("Email already verified. Not Generating Otp...");
            //If token created at is null, it means otp is generated for first time or Otp duration expired and we are setting new Otp
            if(authentication.getTokenCreatedAt() == null || OtpUtils.isEmailOtpExpired(authentication)){
                log.info("Generating Otp...");
                OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository);
            }
            else
                log.error("Email otp already generated");
        } catch (Exception e) {
            log.error("Error ", e);
        }
    }

    @Transactional
    @Override
    public void verifyEmailOtp(String email, String emailOtp) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            String encryptedEmailOtp = authentication.getEmailOtp();

            if(authentication.isEmailVerified()){
                log.error(UserErrorMessages.USER_EMAIL_ALREADY_VERIFIED);
                throw new Exception("Exception " + UserErrorMessages.USER_ALREADY_VERIFIED);
            }

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
                log.info("User Email Verified");
            }
        } catch (Exception e) {
            log.error("Error Occured" + e);
        }
    }

    @Transactional
    @Override
    public void verifyPhone(String email, String countryCode, String phone) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            if(authentication == null)
                throw new Exception("Email doesn't exist");
            if (!authentication.isEmailVerified())
                throw new Exception("Email not verified");
            if(authentication.isPhoneVerified())
                throw new Exception("Phone already verified");
            authentication.setCountryCode(countryCode);
            authentication.setPhone(phone);
            authentication.setPhoneVerified(true);
            authenticationRepository.save(authentication);
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
    }


    /*private List getAuthority() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            if (authentication == null)
                throw new UsernameNotFoundException("Invalid username or password.");
            return new User(authentication.getEmail(), authentication.getToken(), getAuthority());
        } catch (Exception e) {
            log.error("Error occured ", e);
        }
        return null;
    }*/
}
