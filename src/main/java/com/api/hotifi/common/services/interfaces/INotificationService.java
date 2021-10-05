package com.api.hotifi.common.services.interfaces;

import com.api.hotifi.common.processors.codes.CloudClientCodes;

import java.util.List;

public interface INotificationService {

    void sendNotificationToSingleUser(Long userId, String title, String message, CloudClientCodes notificationClientCode);

    void sendPhotoNotificationsToMultipleUsers(List<Long> buyerIds, String title, String message, String commonPhotoUrl, CloudClientCodes notificationClientCode);

    void sendPhotoNotificationsToAllUsers(String title, String message, String photoUrl, CloudClientCodes notificationClientCode);
}
