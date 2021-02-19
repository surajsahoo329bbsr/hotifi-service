package com.api.hotifi.common.services.interfaces;

import com.api.hotifi.common.processors.codes.CloudClientCodes;

import java.util.List;
import java.util.Map;

public interface INotificationService {

    void sendNotification(Long userId, String title, String message, CloudClientCodes notificationClientCode);

    void sendCommonNotifications(List<Long> userIds, String title, String message, CloudClientCodes notificationClientCode);

    void sendPhotoNotification(Long userId, String title, String message, String photoUrl, CloudClientCodes notificationClientCode);

    void sendCommonPhotoNotifications(Map<Long, String> userIdsWithPhotos, String title, String message, CloudClientCodes notificationClientCode);

}
