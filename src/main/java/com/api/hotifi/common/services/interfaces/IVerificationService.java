package com.api.hotifi.common.services.interfaces;

import com.api.hotifi.common.processors.codes.CloudClientCodes;
import com.api.hotifi.common.processors.codes.SocialCodes;
import com.api.hotifi.identity.entities.User;

public interface IVerificationService {

    boolean isSocialUserVerified(String email, String identifier, String token, SocialCodes socialCode);

    boolean isPhoneUserVerified(String countryCode, String phone, String token, CloudClientCodes cloudClientCodes);

    User getSocialUserDetails(String token, SocialCodes socialCode);

}
