package com.api.hotifi.common.services.interfaces;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface IFirebaseMessagingService {

    String sendNotification(String note, String token) throws FirebaseMessagingException;
}
