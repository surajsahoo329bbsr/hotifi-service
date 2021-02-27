package com.api.hotifi.common.processors.social;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.identity.entities.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.auth.FirebaseAuthException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleProcessor {

    public boolean verifyEmail(String email, String token) throws FirebaseAuthException {
        NetHttpTransport netHttpTransport = new NetHttpTransport();
        JacksonFactory jacksonFactory = new JacksonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(netHttpTransport, jacksonFactory)
                .setAudience(Collections.singletonList(Constants.GOOGLE_OAUTH2_SECRET))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(token); // (Receive idTokenString by HTTPS POST)
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String payloadEmail = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                return payloadEmail.equals(email) && emailVerified;
            }

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserDetails(String token) {

        NetHttpTransport netHttpTransport = new NetHttpTransport();
        JacksonFactory jacksonFactory = new JacksonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(netHttpTransport, jacksonFactory)
                .setAudience(Collections.singletonList(Constants.GOOGLE_OAUTH2_SECRET))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                User user = new User();
                user.setGoogleId(payload.getSubject());
                user.setPhotoUrl((String) payload.get("picture"));
                user.setFirstName((String) payload.get("given_name"));
                user.setLastName((String) payload.get("family_name"));
                return user;
            } else {
                System.out.println("Invalid ID token.");
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
