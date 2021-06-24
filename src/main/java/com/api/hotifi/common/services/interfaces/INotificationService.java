package com.api.hotifi.common.services.interfaces;

import com.api.hotifi.common.processors.codes.CloudClientCodes;

import java.util.List;

public interface INotificationService {

    void sendNotification(Long userId, String title, String message, CloudClientCodes notificationClientCode);

    void sendCommonPhotoNotifications(List<Long> buyerIds, String title, String message, String sellerUsername, String commonPhotoUrl, CloudClientCodes notificationClientCode);

    void sendNotificationsToAllUsers(String title, String message, String photoUrl, CloudClientCodes notificationClientCode);
}
