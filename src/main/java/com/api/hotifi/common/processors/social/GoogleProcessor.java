package com.api.hotifi.common.processors.social;

import com.api.hotifi.identity.entities.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
public class GoogleProcessor {

    public boolean verifyEmail(String email, String token) throws FirebaseAuthException {
        NetHttpTransport netHttpTransport = new NetHttpTransport();
        JacksonFactory jacksonFactory = new JacksonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(netHttpTransport, jacksonFactory)
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

    public boolean verifyPhone(String countryCode, String phone, String token) throws FirebaseAuthException {
        NetHttpTransport netHttpTransport = new NetHttpTransport();
        JacksonFactory jacksonFactory = new JacksonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(netHttpTransport, jacksonFactory)
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(token); // (Receive idTokenString by HTTPS POST)
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                log.info("json : ", payload.toString());
                JSONArray jsonArray = (JSONArray) payload.get("users");
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                String verifiedPhoneNumber = jsonObject.get("phoneNumber").toString();
                return verifiedPhoneNumber.equals(countryCode + phone);
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
