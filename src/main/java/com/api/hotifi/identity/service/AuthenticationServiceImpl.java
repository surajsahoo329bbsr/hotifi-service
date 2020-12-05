package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.Authentication;
import com.api.hotifi.identity.error.UserErrorMessages;
import com.api.hotifi.identity.repository.AuthenticationRepository;
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Transactional
    @Override
    //If login client already has email verified no need for further verification
    public void addEmail(String email, boolean isEmailVerified) {
        try {
            Authentication authentication = new Authentication();
            authentication.setEmail(email);
            authentication.setToken(UUID.randomUUID().toString());
            //Below codes implies if email is already verified,
            //then first set the verification email to true and save to DB,
            //else save the object in DB, and then generate email otp to verify email
            if(isEmailVerified) {
                authentication.setEmailVerified(true);
                authenticationRepository.save(authentication);
            } else {
                authenticationRepository.save(authentication);
                generateEmailOtpSignUp(authentication);
            }
        } catch (DataIntegrityViolationException e) {
            log.error(UserErrorMessages.USER_EXISTS);
        } catch (Exception e) {
            log.error("Error Occurred ", e);
        }
    }

    @Override
    public Authentication getAuthentication(String email) {
        try{
            Authentication authentication = authenticationRepository.findByEmail(email);
            if(authentication != null)
                return authentication;
        } catch (Exception e){
            log.error("Error occured ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public void generateEmailOtpSignUp(Authentication authentication) {
        try {
            if (authentication.getEmailOtp() != null) {
                //If time limit is less than 10 minutes
                //Generate new token
                log.error("Email otp already generated");
                return;
            }
            String emailOtp = generateOtp();
            if(emailOtp != null) {
                log.info("Otp " + emailOtp);
                String encryptedEmailOtp = BCrypt.hashpw(emailOtp, BCrypt.gensalt());
                authentication.setEmailOtp(encryptedEmailOtp);
                authenticationRepository.save(authentication); //updating otp in password
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate Email ", e);
        } catch (Exception e) {
            log.error("Error Occurred ", e);
        }
    }

    @Override
    public String generateEmailOtpLogin(String email) {
        try{
            Authentication authentication = authenticationRepository.findByEmail(email);
            if (authentication.getEmailOtp() != null) {
                //If time limit is less than 10 minutes
                //Generate new token
                log.error("Email otp already generated");
                return UserErrorMessages.OTP_ALREADY_GENERATED;
            }
            if (!authentication.isEmailVerified() || !authentication.isPhoneVerified()) {
                log.error("User not verified");
                return UserErrorMessages.USER_ALREADY_NOT_VERIFIED;
            }
            String emailOtp = generateOtp();
            if(emailOtp != null) {
                log.info("Otp " + emailOtp);
                String encryptedEmailOtp = BCrypt.hashpw(emailOtp, BCrypt.gensalt());
                Date now = new Date(System.currentTimeMillis());
                authentication.setEmailOtp(encryptedEmailOtp);
                authentication.setToken(UUID.randomUUID().toString());
                authentication.setTokenCreatedAt(now);
                authenticationRepository.save(authentication); //updating otp in password
                return emailOtp;
            }
        } catch (Exception e){
            log.error("Error ", e);
        }
        return null;
    }

    public String generateOtp(){
        //do stuff
        try {
            TimeBasedOneTimePasswordGenerator timeOtp = new TimeBasedOneTimePasswordGenerator(Duration.ofMinutes(10));

            Key key;
            {
                final KeyGenerator keyGenerator = KeyGenerator.getInstance(timeOtp.getAlgorithm());
                // Key length should match the length of the HMAC output (160 bits for SHA-1, 256 bits
                // for SHA-256, and 512 bits for SHA-512).
                keyGenerator.init(160);
                key = keyGenerator.generateKey();
            }
            //Instant now = Instant.now();
            //final Instant later = now.plus(timeOtp.getTimeStep());

            //System.out.format("Current password: %06d\n", timeOtp.generateOneTimePassword(key, now));
            //System.out.format("Future password:  %06d\n", timeOtp.generateOneTimePassword(key, later));
            return String.valueOf(timeOtp.generateOneTimePassword(key, Instant.now()));
        }
        catch (Exception e){
            log.error("Error ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public boolean verifyEmailOtp(String email, String emailOtp) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            String encryptedEmailOtp = authentication.getEmailOtp();
            Date currentTime = new Date(System.currentTimeMillis());
            long timeDifference = authentication.getTokenCreatedAt().getTime() - currentTime.getTime();
            boolean isOtpExpired = TimeUnit.MILLISECONDS.toMinutes(timeDifference) % 60 >= 10; //Less than 10 minutes

            if(isOtpExpired){
                log.error("Otp Expired");
                authentication.setEmailOtp(null);
                authenticationRepository.save(authentication);
                return false;
            }

            if (BCrypt.checkpw(emailOtp, encryptedEmailOtp)) {
                authentication.setEmailOtp(null);
                authentication.setEmailVerified(true);
                authenticationRepository.save(authentication);
                log.info("User Email Verified");
                return true;
            }
        } catch (Exception e) {
            log.error("Error Occured" + e);
        }
        return false;
    }

    @Override
    public boolean verifyPhoneUser(String email,String countryCode, String phone) {
        try{
            Authentication authentication = authenticationRepository.findByEmail(email);
            if(!authentication.isEmailVerified())
                throw new Exception("Email not verified");
            authentication.setCountryCode(countryCode);
            authentication.setPhone(phone);
            authentication.setPhoneVerified(true);
            authenticationRepository.save(authentication);
            return true;
        } catch (Exception e){
            log.error("Error occurred ", e);
        }
        return false;
    }

    @Override
    public void activateUserEmail(String email, boolean activateUserEmail) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            if (authentication.isActivated() && activateUserEmail) {
                throw new Exception(UserErrorMessages.USER_ALREADY_ACTIVATED);
            } else if (!authentication.isActivated() && !activateUserEmail) {
                throw new Exception(UserErrorMessages.USER_ALREADY_NOT_ACTIVATED);
            } else {
                authentication.setActivated(activateUserEmail);
                authenticationRepository.save(authentication);
            }
        } catch (Exception e) {
            log.error("Error Message ", e);
        }
    }

    @Transactional
    @Override
    public void activateUser(String email, boolean activateUser) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            if (authentication.isActivated() && activateUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_ACTIVATED);
            } else if (!authentication.isActivated() && !activateUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_NOT_ACTIVATED);
            } else {
                authentication.setActivated(activateUser);
                authenticationRepository.save(authentication);
            }
        } catch (Exception e) {
            log.error("Error Message ", e);
        }
    }

    @Transactional
    @Override
    public void banUser(String email, boolean banUser) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            if (authentication.isBanned() && banUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_BANNED);
            } else if (!authentication.isBanned() && !banUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_NOT_BANNED);
            } else {
                authentication.setActivated(banUser);
                authenticationRepository.save(authentication);
            }
        } catch (Exception e) {
            log.error("Error Message ", e);
        }
    }

    @Transactional
    @Override
    public void freezeUser(String email, boolean freezeUser) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            if (authentication.isFreezed() && freezeUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_FREEZED);
            } else if (!authentication.isFreezed() && !freezeUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_NOT_FREEZED);
            } else {
                authentication.setActivated(freezeUser);
                authenticationRepository.save(authentication);
            }
        } catch (Exception e) {
            log.error("Error Message ", e);
        }
    }

    @Transactional
    @Override
    public void deleteUser(String email, boolean deleteUser) {
        try {
            Authentication authentication = authenticationRepository.findByEmail(email);
            if (authentication.isDeleted() && deleteUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_DELETED);
            } else if (!authentication.isDeleted() && !deleteUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_NOT_DELETED);
            } else {
                authentication.setActivated(deleteUser);
                authenticationRepository.save(authentication);
            }
        } catch (Exception e) {
            log.error("Error Message ", e);
        }
    }
}
