package com.api.hotifi.common.utils;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.AuthenticationErrorCodes;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.error.PurchaseErrorCodes;

public class LegitUtils {

    public static boolean isAuthenticationLegit(Authentication authentication) {
        if (authentication == null)
            return false;
        if(!authentication.isEmailVerified())
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_NOT_VERIFIED);
        if(!authentication.isPhoneVerified())
            throw new HotifiException(AuthenticationErrorCodes.PHONE_ALREADY_EXISTS);
        if(!authentication.isActivated())
            throw new HotifiException(UserErrorCodes.USER_NOT_ACTIVATED);
        if(authentication.isDeleted())
            throw new HotifiException(UserErrorCodes.USER_DELETED);
        return true;
    }

    public static boolean isUserLegit(User user) {
        if (user == null)
            return false;
        if(!user.getAuthentication().isActivated())
            throw new HotifiException(UserErrorCodes.USER_NOT_ACTIVATED);
        if(user.getAuthentication().isDeleted())
            throw new HotifiException(UserErrorCodes.USER_DELETED);
        //login check not required because if user has been created then phone and email has been already verified
        return true;
    }

    //Check if buyer is logged in / deleted / activated / freezed / banned / has upi id
    public static boolean isBuyerLegit(User user) {
        if (user == null)
            return false;
        if(!user.getAuthentication().isActivated())
            throw new HotifiException(UserErrorCodes.USER_NOT_ACTIVATED);
        if(user.getAuthentication().isFreezed())
            throw new HotifiException(UserErrorCodes.USER_FREEZED);
        if(user.getAuthentication().isBanned())
            throw new HotifiException(UserErrorCodes.USER_BANNED);
        if(user.getAuthentication().isDeleted())
            throw new HotifiException(UserErrorCodes.USER_DELETED);
        if(!user.isLoggedIn())
            throw new HotifiException(UserErrorCodes.USER_NOT_LOGGED_IN);
        if(user.getUpiId() == null)
            throw new HotifiException(UserErrorCodes.USER_UPI_ID_NOT_ADDED);
        return true;
    }

    //Check if seller is deleted / activated / has upi id
    public static boolean isSellerLegit(User user) {
        if (user == null)
            return false;
        if(!user.getAuthentication().isActivated())
            throw new HotifiException(UserErrorCodes.USER_NOT_ACTIVATED);
        if(user.getAuthentication().isDeleted())
            throw new HotifiException(UserErrorCodes.USER_DELETED);
        if(!user.isLoggedIn())
            throw new HotifiException(UserErrorCodes.USER_NOT_LOGGED_IN);
        if(user.getUpiId() == null)
            throw new HotifiException(UserErrorCodes.USER_UPI_ID_NOT_ADDED);
        return true;
    }

    public static boolean isPurchaseUpdateLegit(Purchase purchase, double dataUsed) {
        if (purchase == null)
            throw new HotifiException(PurchaseErrorCodes.NO_PURCHASE_EXISTS);
        if (purchase.getSessionCreatedAt() == null)
            throw new HotifiException(PurchaseErrorCodes.BUYER_WIFI_SERVICE_NOT_STARTED);
        if (purchase.getSessionFinishedAt() != null)
            throw new HotifiException(PurchaseErrorCodes.BUYER_WIFI_SERVICE_ALREADY_FINISHED);
        if (Double.compare(dataUsed, purchase.getData()) > 0)
            throw new HotifiException(PurchaseErrorCodes.DATA_USED_EXCEEDS_DATA_BOUGHT);
        if (Double.compare(dataUsed, purchase.getDataUsed()) < 0)
            throw new HotifiException(PurchaseErrorCodes.DATA_TO_UPDATE_DECEEDS_DATA_USED);
        return true;
    }
}
