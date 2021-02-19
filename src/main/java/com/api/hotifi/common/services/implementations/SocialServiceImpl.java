package com.api.hotifi.common.services.implementations;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.processors.codes.SocialCodes;
import com.api.hotifi.common.processors.social.FacebookProcessor;
import com.api.hotifi.common.processors.social.GoogleProcessor;
import com.api.hotifi.common.services.interfaces.ISocialService;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.AuthenticationErrorCodes;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocialServiceImpl implements ISocialService {

    public SocialServiceImpl() {
    }

    @Override
    public boolean isSocialUserVerified(String token, String identifier, String email, SocialCodes socialCode) {
        switch (socialCode) {
            case GOOGLE:
                GoogleProcessor googleProcessor = new GoogleProcessor();
                try {
                    return googleProcessor.verifyEmail(email, token);
                } catch (FirebaseAuthException e) {
                    log.error("Error Occured ", e);
                    throw new HotifiException(AuthenticationErrorCodes.FIREBASE_AUTH_EXCEPTION);
                }
            case FACEBOOK:
                FacebookProcessor facebookProcessor = new FacebookProcessor();
                return facebookProcessor.verifyEmail(identifier, token);
        }
        return false;
    }

    @Override
    public User getSocialUserDetails(String token, SocialCodes socialCode) {
        switch (socialCode) {
            case GOOGLE:
                GoogleProcessor googleProcessor = new GoogleProcessor();
                return googleProcessor.getUserDetails(token);
            case FACEBOOK:
                FacebookProcessor facebookProcessor = new FacebookProcessor();
                return facebookProcessor.getUserDetails(token, socialCode.name());
        }
        return null;
    }

}
