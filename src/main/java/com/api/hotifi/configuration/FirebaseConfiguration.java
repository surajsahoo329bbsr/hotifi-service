package com.api.hotifi.configuration;

import com.api.hotifi.common.services.implementations.FirebaseMessagingServiceImpl;
import com.api.hotifi.common.services.interfaces.IFirebaseMessagingService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfiguration {

    @Bean
    public IFirebaseMessagingService firebaseMessagingService(FirebaseMessaging firebaseMessaging){
        return new FirebaseMessagingServiceImpl(firebaseMessaging);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("C:\\Users\\Suraj\\SpringProjects\\hotifi\\src\\main\\resources\\hotifi_app_firebase_service_account.json"));
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "hotifi-app");
        return FirebaseMessaging.getInstance(app);
    }

}
