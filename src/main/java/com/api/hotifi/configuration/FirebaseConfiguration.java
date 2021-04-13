package com.api.hotifi.configuration;

import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.common.services.implementations.FirebaseMessagingServiceImpl;
import com.api.hotifi.common.services.interfaces.IFirebaseMessagingService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfiguration {

    @Bean
    public IFirebaseMessagingService firebaseMessagingService(FirebaseMessaging firebaseMessaging) {
        return new FirebaseMessagingServiceImpl(firebaseMessaging);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(AppConfigurations.GOOGLE_FIREBASE_JSON_FILE_CONFIGURATION_PATH).getInputStream());
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, AppConfigurations.GOOGLE_FIREBASE_PROJECT_NAME);
        return FirebaseMessaging.getInstance(app);
    }

}
