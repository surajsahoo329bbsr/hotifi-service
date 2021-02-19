package com.api.hotifi.common.services.implementations;

import com.api.hotifi.common.processors.codes.CloudClientCodes;
import com.api.hotifi.common.services.interfaces.IFirebaseMessagingService;
import com.api.hotifi.common.services.interfaces.INotificationService;
import com.api.hotifi.identity.entities.Device;
import com.api.hotifi.identity.services.interfaces.IDeviceService;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificationServiceImpl implements INotificationService {

    private final IDeviceService deviceService;
    private final IFirebaseMessagingService firebaseMessagingService;

    public NotificationServiceImpl(IDeviceService deviceService, IFirebaseMessagingService firebaseMessagingService){
        this.deviceService = deviceService;
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @Override
    public void sendNotification(Long userId, String title, String message, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode){
            case GOOGLE_CLOUD_PLATFORM:
                Set<Device> devices = deviceService.getUserDevices(userId);
                devices.forEach(device -> {
                    try {
                        firebaseMessagingService.sendNotification("note", device.getToken());
                    } catch (FirebaseMessagingException e) {
                        e.printStackTrace();
                    }
                });
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

    @Override
    public void sendCommonNotifications(List<Long> userIds, String title, String message, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode){
            case GOOGLE_CLOUD_PLATFORM:
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

    @Override
    public void sendPhotoNotification(Long userId, String title, String message, String photoUrl, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode){
            case GOOGLE_CLOUD_PLATFORM:
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

    @Override
    public void sendCommonPhotoNotifications(Map<Long, String> userIdsWithPhotos, String title, String message, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode){
            case GOOGLE_CLOUD_PLATFORM:
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }
}
