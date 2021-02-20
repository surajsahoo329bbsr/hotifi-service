package com.api.hotifi.common.services.interfaces;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface IFirebaseMessagingService {

    void sendNotification(String subject, String content, String token) throws FirebaseMessagingException;

    void sendPhotoNotification(String subject, String content, String photoUrl, String token) throws FirebaseMessagingException;

}
