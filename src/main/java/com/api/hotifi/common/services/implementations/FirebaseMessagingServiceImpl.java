package com.api.hotifi.common.services.implementations;

import com.api.hotifi.common.services.interfaces.IFirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

public class FirebaseMessagingServiceImpl implements IFirebaseMessagingService {

    private final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingServiceImpl(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    @Override
    public String sendNotification(String note, String token) throws FirebaseMessagingException {

        Notification notification = Notification
                .builder()
                //.setTitle(note.getSubject())
                //.setBody(note.getContent())
                .build();

        Message message = Message
                .builder()
                .setToken(token)
                //.setNotification(notification)
                //.putAllData(note.getData())
                .build();

        return firebaseMessaging.send(message);
    }
}
