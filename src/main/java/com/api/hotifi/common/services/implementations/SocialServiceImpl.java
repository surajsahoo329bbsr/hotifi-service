package com.api.hotifi.common.services.implementations;

import com.api.hotifi.common.processors.codes.SocialCodes;
import com.api.hotifi.common.services.interfaces.ISocialService;
import com.api.hotifi.identity.entities.User;

public class SocialServiceImpl implements ISocialService {

    public SocialServiceImpl(){
    }

    @Override
    public void verifySocialUser(String idToken, SocialCodes socialCode) {
        switch (socialCode){
            case GOOGLE:

            case FACEBOOK:
        }
    }

    @Override
    public User getSocialUserDetails(String idToken, SocialCodes socialCode) {
        switch (socialCode){
            case GOOGLE:

            case FACEBOOK:
        }
        return null;
    }

}
