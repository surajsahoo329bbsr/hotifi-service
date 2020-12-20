package com.api.hotifi.common.utils;

import com.api.hotifi.identity.entity.User;

public class LegitUtils {

    public static boolean isUserLegit(User user) {
        if (user == null)
            return false;
        //login check not required because if user has been created then phone and email has been already verified
        return !user.getAuthentication().isDeleted() && user.getAuthentication().isActivated();
    }

    //Check if buyer is deleted / activated / freezed / banned
    public static boolean isBuyerLegit(User user){
        if (user == null)
            return false;
        return !user.getAuthentication().isDeleted() && user.getAuthentication().isActivated() && !user.getAuthentication().isFreezed() && !user.getAuthentication().isBanned();
    }
}
