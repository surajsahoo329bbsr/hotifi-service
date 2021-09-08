package com.api.hotifi.common.processors.social;

import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.identity.entities.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FacebookProcessor {

    public boolean verifyEmail(String identifier, String userToken){
        RestTemplate restTemplate = new RestTemplate();
        String appUrl = AppConfigurations.FACEBOOK_GRAPH_API_URL + "/debug_token?input_token=" + userToken
                + "&access_token=" + AppConfigurations.FACEBOOK_APP_ID + "|" + AppConfigurations.FACEBOOK_APP_SECRET;
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(appUrl, String.class);
            ObjectMapper mapper = new ObjectMapper();
            //log.info("response : " + responseEntity.getBody());
            JsonNode root = mapper.readTree(responseEntity.getBody());
            String facebookId = root.path("data").path("user_id").toString().replaceAll("\"","");
            //log.info("fbId " + facebookId);
            //log.info("id " + identifier);
            return facebookId.equals(identifier);
        } catch (Exception e){
            log.error("Error Occurred", e);
        }
        return false;
    }

    //TODO fetch user profile information using Facebook's Graph Api
    public User getUserDetails(String userToken, String identifier){
        return null;
    }

    /**
     {
     "algorithm": "HMAC-SHA256",
     "expires": 1291840400,
     "issued_at": 1291836800,
     "user_id": "218471"
     }
     Below method's JSONObject returns this JSON Response
     */

    public JSONObject parseFacebookSignedRequest(String signedRequest, String secret) throws UnsupportedEncodingException, Exception {
        //split request into signature and data
        String[] signedRequests = signedRequest.split("\\.", 2);
        //parse signature
        String signature = signedRequests[0];

        //parse data and convert to json object
        String data = signedRequests[1];

        //I assumed it is UTF8
        JSONObject jsonData = new JSONObject(new String(Base64.decodeBase64(data), StandardCharsets.UTF_8));
        //check signature algorithm
        if(!jsonData.getString("algorithm").equals("HMAC-SHA256")) {
            //unknown algorithm is used
            return null;
        }

        //check if data is signed correctly
        if(!hmacSHA256(signedRequests[1], secret).equals(signature)) {
            //signature is not correct, possibly the data was tampered with
            return null;
        }
        return jsonData;
    }

    //HmacSHA256 implementation
    private String hmacSHA256(String data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] hmacData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.encodeBase64URLSafe(hmacData), StandardCharsets.UTF_8);
    }

}
