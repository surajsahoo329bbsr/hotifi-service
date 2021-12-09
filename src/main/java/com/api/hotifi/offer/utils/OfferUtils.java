package com.api.hotifi.offer.utils;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import static com.api.hotifi.common.constants.configurations.BusinessConfigurations.*;

public class OfferUtils {

    /**
     * Generates referral codes of size 10 for first 99999 (5 digit) referrers.
     * Beyond this it won't work, returns null
     * Pattern will be @param - BusinessConfigurations.REFERRAL_CODE_PREFIX (3 length) + alphanumeric code (7 length)
     * example - REFLB1CN20
     **/

    public static String generateReferralCode(Long referrerUserId) {

        if (referrerUserId == null || referrerUserId < 0 || referrerUserId > 99999) return null;

        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        int referralCodeNonPrefixLength = REFERRAL_CODE_LENGTH - REFERRAL_CODE_PREFIX.length();
        String userId = String.valueOf(referrerUserId);
        int userIdLength = userId.length();
        char[] chars = CAPITAL_LETTERS.toCharArray();
        int charLength = chars.length;
        int numberCount = 0;

        for (int min = 0; min < referralCodeNonPrefixLength; min++) {
            int max = userIdLength - numberCount < referralCodeNonPrefixLength - min ?
                    referralCodeNonPrefixLength - userIdLength + min:  min + 1;
            /*
              min value is always increasing like time, but max value will be decided
              if the complete userId has been appended to referral code or not
            */
            int randomCharInt = ThreadLocalRandom.current().nextInt(min, max);

            if(numberCount < userIdLength && min == randomCharInt){
                char numChar = userId.charAt(numberCount);
                stringBuilder.append(numChar);
                numberCount++;
                continue;
            }

            int randomNumberInt = secureRandom.nextInt(charLength);
            char randomChar = chars[randomNumberInt];
            stringBuilder.append(randomChar);
        }

        return REFERRAL_CODE_PREFIX + stringBuilder;
    }

    /*public static String generateReferralCode(Long referrerUserId) {
        if (referrerUserId == null || referrerUserId < 0 || referrerUserId > 99999) return null;

        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        int referralCodeNonPrefixLength = REFERRAL_CODE_LENGTH - REFERRAL_CODE_PREFIX.length();
        String userId = String.valueOf(referrerUserId);
        char[] chars = CAPITAL_LETTERS.toCharArray();
        int charLength = chars.length;

        for (int i = 0; i < referralCodeNonPrefixLength; i++) {
            int randomNumberInt = secureRandom.nextInt(charLength);
            char randomChar = chars[randomNumberInt];
            stringBuilder.append(randomChar);
        }

        int randomCharInt = 0;

        for (int i = 0; i < userId.length(); i++) {
            int max = referralCodeNonPrefixLength - userId.length() + i;
            int min = randomCharInt + 1;
            randomCharInt = ThreadLocalRandom.current().nextInt(min, max + 1);
            char numChar = userId.charAt(i);
            stringBuilder.setCharAt(randomCharInt, numChar);
        }

        return REFERRAL_CODE_PREFIX + stringBuilder;
    }*/

}
