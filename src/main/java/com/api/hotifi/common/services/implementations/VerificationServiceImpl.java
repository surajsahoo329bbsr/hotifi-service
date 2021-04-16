package com.api.hotifi.common.services.implementations;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.processors.codes.CloudClientCodes;
import com.api.hotifi.common.processors.codes.SocialCodes;
import com.api.hotifi.common.processors.social.FacebookProcessor;
import com.api.hotifi.common.processors.social.GoogleProcessor;
import com.api.hotifi.common.services.interfaces.IVerificationService;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.AuthenticationErrorCodes;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VerificationServiceImpl implements IVerificationService {

    private final GoogleProcessor googleProcessor;
    private final FacebookProcessor facebookProcessor;

    public VerificationServiceImpl() {
        googleProcessor = new GoogleProcessor();
        facebookProcessor = new FacebookProcessor();
    }

    @Override
    public boolean isSocialUserVerified(String email, String identifier, String token, SocialCodes socialCode) {
        switch (socialCode) {
            case GOOGLE:
                try {
                    return googleProcessor.verifyEmail(email, token);
                } catch (FirebaseAuthException e) {
                    log.error("Error occurred ", e);
                    throw new HotifiException(AuthenticationErrorCodes.FIREBASE_AUTH_EXCEPTION);
                }
            case FACEBOOK:
                return facebookProcessor.verifyEmail(identifier, token);
        }
        return false;
    }

    @Override
    public boolean isPhoneUserVerified(String countryCode, String phone, String token, CloudClientCodes cloudClientCodes) {
        switch (cloudClientCodes) {
            case GOOGLE_CLOUD_PLATFORM:
                try {
                    return googleProcessor.verifyPhone(countryCode, phone, token);
                } catch (Exception e) {
                    log.error("Error occurred ", e);
                    throw new HotifiException(AuthenticationErrorCodes.FIREBASE_AUTH_EXCEPTION);
                }
            case AMAZON_WEB_SERVICES:
            case AZURE:
            case TWILIO:
        }
        return false;
    }

    @Override
    public User getSocialUserDetails(String token, SocialCodes socialCode) {
        switch (socialCode) {
            case GOOGLE:
                return googleProcessor.getUserDetails(token);
            case FACEBOOK:
                return facebookProcessor.getUserDetails(token, socialCode.name());
        }
        return null;
    }

}
