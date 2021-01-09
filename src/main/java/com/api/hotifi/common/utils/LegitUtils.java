package com.api.hotifi.common.utils;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.error.PurchaseErrorCodes;

public class LegitUtils {

    public static boolean isAuthenticationLegit(Authentication authentication) {
        if (authentication == null)
            return false;
        return authentication.isEmailVerified() && authentication.isPhoneVerified()
                && authentication.isActivated() && !authentication.isDeleted();
    }

    public static boolean isUserLegit(User user) {
        if (user == null)
            return false;
        //login check not required because if user has been created then phone and email has been already verified
        return !user.getAuthentication().isDeleted() && user.getAuthentication().isActivated();
    }

    //Check if buyer is logged in / deleted / activated / freezed / banned / has upi id
    public static boolean isBuyerLegit(User user) {
        if (user == null)
            return false;
        return !user.getAuthentication().isDeleted() && user.isLoggedIn() && user.getAuthentication().isActivated() && !user.getAuthentication().isFreezed() && !user.getAuthentication().isBanned() && user.getUpiId() != null;
    }

    //Check if seller is deleted / activated / has upi id
    public static boolean isSellerLegit(User user) {
        if (user == null)
            return false;
        return !user.getAuthentication().isDeleted() && user.isLoggedIn() && user.getAuthentication().isActivated() && user.getUpiId() != null;
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
