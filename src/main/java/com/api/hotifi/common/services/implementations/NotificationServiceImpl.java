package com.api.hotifi.common.services.implementations;

import com.api.hotifi.common.processors.codes.CloudClientCodes;
import com.api.hotifi.common.services.interfaces.IFirebaseMessagingService;
import com.api.hotifi.common.services.interfaces.INotificationService;
import com.api.hotifi.identity.entities.Device;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.DeviceRepository;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.services.interfaces.IDeviceService;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NotificationServiceImpl implements INotificationService {

    private final DeviceRepository deviceRepository;
    private final IDeviceService deviceService;
    private final UserRepository userRepository;
    private final IFirebaseMessagingService firebaseMessagingService;

    public NotificationServiceImpl(DeviceRepository deviceRepository, IDeviceService deviceService, UserRepository userRepository, IFirebaseMessagingService firebaseMessagingService) {
        this.deviceRepository = deviceRepository;
        this.deviceService = deviceService;
        this.userRepository = userRepository;
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

    @Override
    public void sendNotificationsToAllUsers(String title, String message, String photoUrl, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode) {
            case GOOGLE_CLOUD_PLATFORM:
                /*List<Long> userIds = userRepository.findAll().stream()
                        .filter(user -> user.getAuthentication().isActivated() && !user.getAuthentication().isDeleted()
                                && !user.getAuthentication().isBanned() && !user.getAuthentication().isFreezed())
                        .map(User::getId)
                        .collect(Collectors.toList());*/
                List<Device> devices = deviceRepository.findAll();
                devices.forEach(device -> {
                    try {
                        firebaseMessagingService.sendPhotoNotification(title, message, photoUrl, device.getToken());
                    } catch (FirebaseMessagingException e) {
                        e.printStackTrace();
                    }
                });
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

}
