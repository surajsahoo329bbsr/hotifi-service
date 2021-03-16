package com.api.hotifi.authorization.service;

public interface ICustomerAuthorizationService {

    boolean isAuthorizedByAndroidId(String androidId, String bearerToken);

    boolean isAuthorizedByUserId(Long userId, String bearerToken);

    boolean isAuthorizedByEmail(String email, String bearerToken);

    boolean isAuthorizedByUsername(String username, String bearerToken);

    boolean isAuthorizedBySocialId(String socialId, String bearerToken);

    boolean isAuthorizedBySessionId(Long sessionId, String bearerToken);

    boolean isAuthorizedByAuthenticationId(Long authenticationId, String bearerToken);

    boolean isAuthorizedByPurchaseId(Long purchaseId, String bearerToken);

    boolean isAuthorizedBySellerReceiptId(Long sellerReceiptId, String bearerToken);

}
