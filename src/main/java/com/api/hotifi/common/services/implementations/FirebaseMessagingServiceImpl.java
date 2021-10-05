package com.api.hotifi.common.services.implementations;

import com.api.hotifi.common.services.interfaces.IFirebaseMessagingService;
import com.google.firebase.messaging.*;

import java.util.List;

public class FirebaseMessagingServiceImpl implements IFirebaseMessagingService {

    private final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingServiceImpl(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    @Override
    public void sendNotificationToSingleUser(String subject, String content, String token) throws FirebaseMessagingException {

        Notification notification = Notification
                .builder()
                .setTitle(subject)
                .setBody(content)
                .build();

        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        firebaseMessaging.send(message);
    }

    @Override
    public void sendPhotoNotificationToSingleUser(String subject, String content, String photoUrl, String token) throws FirebaseMessagingException {
        Notification notification = Notification
                .builder()
                .setTitle(subject)
                .setBody(content)
                .setImage(photoUrl)
                .build();

        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .build();


        firebaseMessaging.send(message);
    }

    @Override
    public void sendPhotoNotificationToMultipleUsers(String subject, String content, String photoUrl, List<String> fcmTokens) throws FirebaseMessagingException {
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(subject)
                        .setBody(content)
                        .setImage(photoUrl)
                        .build())
                .addAllTokens(fcmTokens)
                .build();

        firebaseMessaging.sendMulticast(multicastMessage);
    }

    @Override
    public void sendNotificationToMultipleUsers(String subject, String content, List<String> fcmTokens) throws FirebaseMessagingException {
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(subject)
                        .setBody(content)
                        .build())
                .addAllTokens(fcmTokens)
                .build();

        firebaseMessaging.sendMulticast(multicastMessage);
    }
}
