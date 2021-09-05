package com.api.hotifi.common.processors.social;

import com.api.hotifi.common.constants.configurations.AppConfigurations;
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

    public boolean verifyPhone(String countryCode, String phone, String token) {
        String url = AppConfigurations.FIREBASE_GET_ACCOUNT_INFO_URL + "?key=" + AppConfigurations.GOOGLE_FIREBASE_ANDROID_CLIENT_SECRET;
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
            Response response = client.newCall(request).execute();
            if (response.code() == 200 && response.body() != null) {
                String jsonString = response.body().string(); //assign your JSON String here
                JSONObject obj = new JSONObject(jsonString);
                JSONArray array = obj.getJSONArray("users"); // notice that `"posts": [...]`
                String phoneNumber = array.getJSONObject(0).getString("phoneNumber");
                return phoneNumber.contains("+" + countryCode + phone);
            }
        } catch (Exception e) {
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
                log.error("Invalid ID token");
                //System.out.println("Invalid ID token.");
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
