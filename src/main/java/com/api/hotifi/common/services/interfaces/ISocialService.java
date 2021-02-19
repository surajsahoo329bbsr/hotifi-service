package com.api.hotifi.common.services.interfaces;

import com.api.hotifi.common.processors.codes.SocialCodes;
import com.api.hotifi.identity.entities.User;

public interface ISocialService {

    boolean isSocialUserVerified(String email, String identifier, String token, SocialCodes socialCode);

    User getSocialUserDetails(String token, SocialCodes socialCode);

}
