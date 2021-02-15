package com.api.hotifi.common.services.interfaces;

import com.api.hotifi.common.processors.codes.SocialCodes;
import com.api.hotifi.identity.entities.User;

public interface ISocialService {

    void verifySocialUser(String idToken, SocialCodes socialCode);

    User getSocialUserDetails(String idToken, SocialCodes socialCode);

}
