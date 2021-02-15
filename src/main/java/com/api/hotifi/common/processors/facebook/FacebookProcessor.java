package com.api.hotifi.common.processors.facebook;

import com.api.hotifi.common.constant.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class FacebookProcessor {

    public boolean verifyFacebookIdentifier(String userToken, String identifier){
        RestTemplate restTemplate = new RestTemplate();
        String appUrl = Constants.FACEBOOK_GRAPH_API_URL + "/debug_token?input_token=" + userToken
                + "&access_token=" + Constants.FACEBOOK_CLIENT_ID + "|" +Constants.FACEBOOK_CLIENT_KEY;
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(appUrl, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseEntity.getBody());
            String facebookId = root.path("data").path("user_id").toString();
            return facebookId.equals(identifier);
        } catch (Exception e){
            log.error("Error Occurred", e);
        }
        return false;
    }

}
