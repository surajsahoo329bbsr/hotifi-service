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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public void sendNotificationToSingleUser(Long userId, String title, String message, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode) {
            case GOOGLE_CLOUD_PLATFORM:
                Optional<String> optional = deviceService.getUserDevices(userId)
                        .stream().map(Device::getToken)
                        .reduce((first, second) -> second);
                String fcmToken = optional.orElse(null);

                try {
                    firebaseMessagingService.sendNotificationToSingleUser(title, message, fcmToken);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                }
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

    @Override
    public void sendPhotoNotificationsToMultipleUsers(List<Long> buyerIds, String title, String message, String commonPhotoUrl, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode) {
            case GOOGLE_CLOUD_PLATFORM:
                List<String> fcmTokens = deviceRepository.findAllById(buyerIds).stream()
                        .map(Device::getToken)
                        .collect(Collectors.toList());
                try {
                    firebaseMessagingService.sendPhotoNotificationToMultipleUsers(title, message, commonPhotoUrl, fcmTokens);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                }
            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

    @Override
    public void sendPhotoNotificationsToAllUsers(String title, String message, String photoUrl, CloudClientCodes notificationClientCode) {
        switch (notificationClientCode) {
            case GOOGLE_CLOUD_PLATFORM:
                /*List<Long> userIds = userRepository.findAll().stream()
                        .filter(user -> user.getAuthentication().isActivated() && !user.getAuthentication().isDeleted()
                                && !user.getAuthentication().isBanned() && !user.getAuthentication().isFreezed())
                        .map(User::getId)
                        .collect(Collectors.toList());*/
                List<String> fcmTokens = deviceRepository.findAll()
                        .stream().map(Device::getToken)
                        .collect(Collectors.toList());
                try {
                    firebaseMessagingService.sendPhotoNotificationToMultipleUsers(title, message, photoUrl, fcmTokens);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                }

            case AMAZON_WEB_SERVICES:
            case AZURE:
        }
    }

}
