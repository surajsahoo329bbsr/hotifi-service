package com.api.hotifi.identity.utils;

import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class OtpUtils {

    public static String generateEmailOtp() {
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
        } catch (Exception e) {
            log.error("Error ", e);
        }
        return null;
    }

    public static boolean isEmailOtpExpired(Authentication authentication){
        Date currentTime = new Date(System.currentTimeMillis());
        long timeDifference =  currentTime.getTime() - authentication.getTokenCreatedAt().getTime();
        long minutesDifference = timeDifference / (60L * 1000L);
        return minutesDifference >= 10; // If otp generated is more than 10 minutes
    }

    //needs to be called from generateEmailOtpSignUp or generateEmailOtpLogin
    public static void saveAuthenticationEmailOtp(Authentication authentication, AuthenticationRepository authenticationRepository){
        String emailOtp = generateEmailOtp();
        String token = UUID.randomUUID().toString();
        if (emailOtp != null) {
            log.info("Otp " + emailOtp);
            log.info("Token" + token);
            String encryptedEmailOtp = BCrypt.hashpw(emailOtp, BCrypt.gensalt());
            String encryptedToken = BCrypt.hashpw(token, BCrypt.gensalt());
            Date now = new Date(System.currentTimeMillis()); //set updated token created time
            authentication.setTokenCreatedAt(now);
            authentication.setToken(encryptedToken);
            authentication.setEmailOtp(encryptedEmailOtp);
            authenticationRepository.save(authentication); //updating otp in password
        }
    }

}
