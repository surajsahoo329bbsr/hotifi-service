package com.api.hotifi.common.services.implementations;

import com.api.hotifi.common.processors.codes.CloudClientCodes;
import com.api.hotifi.common.services.interfaces.IFirebaseMessagingService;
import com.api.hotifi.common.services.interfaces.INotificationService;
import com.api.hotifi.identity.entities.Device;
import com.api.hotifi.identity.repositories.DeviceRepository;
import com.api.hotifi.identity.services.interfaces.IDeviceService;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;
import java.util.Set;

public class NotificationServiceImpl implements INotificationService {

    private final DeviceRepository deviceRepository;
    private final IDeviceService deviceService;
    private final IFirebaseMessagingService firebaseMessagingService;

    public NotificationServiceImpl(DeviceRepository deviceRepository, IDeviceService deviceService, IFirebaseMessagingService firebaseMessagingService) {
        this.deviceRepository = deviceRepository;
        this.deviceService = deviceService;
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @Override
    public void sendNotification(Long userId, String title, String message, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode) {
            case GOOGLE_CLOUD_PLATFORM:
                Set<Device> devices = deviceService.getUserDevices(userId);
                devices.forEach(device -> {
                    try {
                        firebaseMessagingService.sendNotification(title, message, device.getToken());
                    } catch (FirebaseMessagingException e) {
                        e.printStackTrace();
                    }
                });
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

    @Override
    public void sendCommonPhotoNotifications(List<Long> buyerIds, String title, String message, String sellerUsername, String commonPhotoUrl, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode) {
            case GOOGLE_CLOUD_PLATFORM:
                List<Device> devices = deviceRepository.findAllById(buyerIds);
                devices.forEach(device -> {
                    try {
                        firebaseMessagingService.sendPhotoNotification(sellerUsername + message, title, commonPhotoUrl, device.getToken());
                    } catch (FirebaseMessagingException e) {
                        e.printStackTrace();
                    }
                });
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

}
