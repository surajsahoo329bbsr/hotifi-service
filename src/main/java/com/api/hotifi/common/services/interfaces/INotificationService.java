package com.api.hotifi.common.services.interfaces;

import com.api.hotifi.common.processors.codes.CloudClientCodes;

import java.util.List;
import java.util.Map;

public interface INotificationService {

    void sendNotification(String userId, String title, String message, CloudClientCodes notificationClientCode);

    void sendCommonNotifications(List<String> userIds, String title, String message, CloudClientCodes notificationClientCode);

    void sendPhotoNotification(String userId, String title, String message, String photoUrl, CloudClientCodes notificationClientCode);

    void sendCommonPhotoNotifications(Map<String, String> userIdsWithPhotos, String title, String message, CloudClientCodes notificationClientCode);

}
