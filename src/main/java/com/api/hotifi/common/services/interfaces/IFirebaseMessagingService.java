package com.api.hotifi.common.services.interfaces;

import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;

public interface IFirebaseMessagingService {

    void sendNotificationToSingleUser(String subject, String content, String token) throws FirebaseMessagingException;

    void sendPhotoNotificationToSingleUser(String subject, String content, String photoUrl, String token) throws FirebaseMessagingException;

    void sendNotificationToMultipleUsers(String subject, String content, List<String> fcmTokens) throws  FirebaseMessagingException;

    void sendPhotoNotificationToMultipleUsers(String subject, String content, String photoUrl, List<String> fcmTokens) throws  FirebaseMessagingException;
}
