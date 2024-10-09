package com.api.hotifi.common.processors.social;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.models.GoogleIdTokenJson;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
public class GoogleProcessor {

    @Value("{google.firebase.android.secret}")
    private String firebaseAndroidClientSecret;

    @Value("{google.firebase.file-name}")
    private String firebaseFileName;

    private static final Logger logger = LoggerFactory.getLogger(GoogleProcessor.class);

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
            logger.error("An error occurred : {}", e.getMessage(), e);

        }
        return false;
    }

    public boolean verifyPhone(String countryCode, String phone, String token) {
        String url = firebaseFileName + "?key=" + firebaseAndroidClientSecret;
        GoogleIdTokenJson googleIdToken = new GoogleIdTokenJson(token);
        String json = new Gson().toJson(googleIdToken);
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try(Response response = client.newCall(request).execute()){
                if (response.code() == 200 && response.body() != null) {
                    String jsonString = response.body().string(); //assign your JSON String here
                    JSONObject obj = new JSONObject(jsonString);
                    JSONArray array = obj.getJSONArray("users"); // notice that `"posts": [...]`
                    String phoneNumber = array.getJSONObject(0).getString("phoneNumber");
                    return phoneNumber.contains("+" + countryCode + phone);
                }
            } catch (Exception e){
                logger.error("An error occurred : {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error("An error occurred : {}", e.getMessage(), e);
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
                log.error("Invalid ID token");
                //System.out.println("Invalid ID token.");
            }
        } catch (GeneralSecurityException | IOException e) {
            logger.error("An error occurred : {}", e.getMessage(), e);
        }
        return null;
    }

}
