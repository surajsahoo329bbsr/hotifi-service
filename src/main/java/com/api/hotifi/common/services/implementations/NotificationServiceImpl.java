package com.api.hotifi.common.services.implementations;

import com.api.hotifi.common.processors.codes.CloudClientCodes;
import com.api.hotifi.common.services.interfaces.INotificationService;
import com.api.hotifi.identity.services.interfaces.IDeviceService;

import java.util.List;
import java.util.Map;

public class NotificationServiceImpl implements INotificationService {

    private final IDeviceService deviceService;

    public NotificationServiceImpl(IDeviceService deviceService){
        this.deviceService = deviceService;
    }

    @Override
    public void sendNotification(String userId, String title, String message, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode){
            case GOOGLE_CLOUD_PLATFORM:
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

    @Override
    public void sendCommonNotifications(List<String> userIds, String title, String message, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode){
            case GOOGLE_CLOUD_PLATFORM:
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

    @Override
    public void sendPhotoNotification(String userId, String title, String message, String photoUrl, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode){
            case GOOGLE_CLOUD_PLATFORM:
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

    @Override
    public void sendCommonPhotoNotifications(Map<String, String> userIdsWithPhotos, String title, String message, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode){
            case GOOGLE_CLOUD_PLATFORM:
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }
}
