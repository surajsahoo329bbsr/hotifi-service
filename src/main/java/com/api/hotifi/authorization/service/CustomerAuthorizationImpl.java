package com.api.hotifi.authorization.service;


import com.api.hotifi.authorization.jwt.JwtDecoder;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.Device;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.DeviceErrorCodes;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.api.hotifi.identity.repositories.DeviceRepository;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerReceipt;
import com.api.hotifi.payment.error.PurchaseErrorCodes;
import com.api.hotifi.payment.error.SellerPaymentErrorCodes;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.repositories.SellerReceiptRepository;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.error.SessionErrorCodes;
import com.api.hotifi.session.repository.SessionRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerAuthorizationImpl implements ICustomerAuthorizationService {

    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PurchaseRepository purchaseRepository;
    private final SellerReceiptRepository sellerReceiptRepository;
    private final DeviceRepository deviceRepository;
    private final JwtDecoder jwtDecoder;

    public CustomerAuthorizationImpl(AuthenticationRepository authenticationRepository, UserRepository userRepository, SessionRepository sessionRepository, PurchaseRepository purchaseRepository, SellerReceiptRepository sellerReceiptRepository, DeviceRepository deviceRepository, JwtDecoder jwtDecoder) {
        this.authenticationRepository = authenticationRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.purchaseRepository = purchaseRepository;
        this.sellerReceiptRepository = sellerReceiptRepository;
        this.deviceRepository = deviceRepository;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public boolean isAuthorizedByAndroidId(String androidId, String bearerToken) {
        Device device = deviceRepository.findByAndroidId(androidId);
        if (device == null)
            throw new HotifiException(DeviceErrorCodes.ANDROID_ID_NOT_FOUND);
        String jwtUsername = jwtDecoder.extractUsername(bearerToken);
        if (jwtUsername == null)
            throw new HotifiException(DeviceErrorCodes.FORBIDDEN_ANDROID_DEVICE_ID);
        List<User> users = device.getUsers()
                .stream()
                .filter(user -> user.getAuthentication().getEmail().equals(jwtUsername))
                .collect(Collectors.toList());
        return users.size() > 0;
    }

    @Override
    public boolean isAuthorizedByUserId(Long userId, String bearerToken) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        String jwtUsername = jwtDecoder.extractUsername(bearerToken);
        if (jwtUsername == null || !jwtUsername.equals(user.getAuthentication().getEmail()))
            throw new HotifiException(UserErrorCodes.USER_FORBIDDEN);
        if (jwtDecoder.isTokenExpired(bearerToken))
            throw new HotifiException(UserErrorCodes.USER_TOKEN_EXPIRED);
        return true;
    }

    @Override
    public boolean isAuthorizedByEmail(String email, String bearerToken) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        User user = authentication != null ? userRepository.findByAuthenticationId(authentication.getId()) : null;
        if (user == null)
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        String jwtUsername = jwtDecoder.extractUsername(bearerToken);
        if (jwtUsername == null || !jwtUsername.equals(user.getAuthentication().getEmail()))
            throw new HotifiException(UserErrorCodes.USER_FORBIDDEN);
        if (jwtDecoder.isTokenExpired(bearerToken))
            throw new HotifiException(UserErrorCodes.USER_TOKEN_EXPIRED);
        return true;
    }

    @Override
    public boolean isAuthorizedByUsername(String username, String bearerToken) {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        String jwtUsername = jwtDecoder.extractUsername(bearerToken);
        if (jwtUsername == null)
            throw new HotifiException(UserErrorCodes.USER_FORBIDDEN);
        return jwtUsername.equals(user.getAuthentication().getEmail());
    }

    @Override
    public boolean isAuthorizedBySocialId(String socialId, String bearerToken) {
        User user = userRepository.findByFacebookId(socialId);
        if (user == null) {
            user = userRepository.findByGoogleId(socialId);
            if (user == null)
                throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        }
        String jwtUsername = jwtDecoder.extractUsername(bearerToken);
        if (jwtUsername == null)
            throw new HotifiException(UserErrorCodes.USER_FORBIDDEN);
        return jwtUsername.equals(user.getAuthentication().getEmail());
    }

    @Override
    public boolean isAuthorizedBySessionId(Long sessionId, String bearerToken) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null)
            throw new HotifiException(SessionErrorCodes.SESSION_NOT_FOUND);
        String jwtUsername = jwtDecoder.extractUsername(bearerToken);
        if (jwtUsername == null)
            throw new HotifiException(UserErrorCodes.USER_FORBIDDEN);
        return jwtUsername
                .equals(session
                        .getSpeedTest()
                        .getUser()
                        .getAuthentication().getEmail());
    }

    @Override
    public boolean isAuthorizedByAuthenticationId(Long authenticationId, String bearerToken) {
        User user = userRepository.findByAuthenticationId(authenticationId);
        if (user == null)
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        String jwtUsername = jwtDecoder.extractUsername(bearerToken);
        if (jwtUsername == null)
            throw new HotifiException(UserErrorCodes.USER_FORBIDDEN);
        return jwtUsername.equals(user.getAuthentication().getEmail());
    }

    @Override
    public boolean isAuthorizedByPurchaseId(Long purchaseId, String bearerToken) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
        if (purchase == null)
            throw new HotifiException(PurchaseErrorCodes.PURCHASE_NOT_FOUND);
        String jwtUsername = jwtDecoder.extractUsername(bearerToken);
        if (jwtUsername == null)
            throw new HotifiException(UserErrorCodes.USER_FORBIDDEN);
        return jwtUsername
                .equals(purchase
                        .getUser()
                        .getAuthentication().getEmail());
    }

    @Override
    public boolean isAuthorizedBySellerReceiptId(Long sellerReceiptId, String bearerToken) {
        SellerReceipt sellerReceipt = sellerReceiptRepository.findById(sellerReceiptId).orElse(null);
        if (sellerReceipt == null)
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_RECEIPT_NOT_FOUND);
        String jwtUsername = jwtDecoder.extractUsername(bearerToken);
        if (jwtUsername == null)
            throw new HotifiException(UserErrorCodes.USER_FORBIDDEN);
        return jwtUsername
                .equals(sellerReceipt
                        .getSellerPayment().getSeller()
                        .getAuthentication().getEmail());
    }
}
