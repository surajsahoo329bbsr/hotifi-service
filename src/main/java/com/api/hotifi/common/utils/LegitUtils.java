package com.api.hotifi.common.utils;

import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.payment.entities.Purchase;

public class LegitUtils {

    public static boolean isAuthenticationLegit(Authentication authentication){
        if(authentication == null)
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

    public static boolean isPurchaseUpdateLegit(Purchase purchase, double dataUsed) throws Exception {
        if (purchase == null)
            throw new Exception("Purchase to be updated doesn't exist");
        if (purchase.getSessionCreatedAt() == null)
            throw new Exception("Buyer's wifi service not started");
        if (purchase.getSessionFinishedAt() != null)
            throw new Exception("Buyer's wifi service already finished");
        if (Double.compare(dataUsed, purchase.getData()) > 0)
            throw new Exception("Data used " + dataUsed + " MB cannot be greater than data bought " + purchase.getData());
        if (Double.compare(dataUsed, purchase.getDataUsed()) < 0)
            throw new Exception("New Data used " + dataUsed + " MB cannot be less than data used " + purchase.getDataUsed());
        return true;
    }
}
