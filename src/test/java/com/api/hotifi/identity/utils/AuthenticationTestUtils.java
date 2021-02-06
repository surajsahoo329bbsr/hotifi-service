package com.api.hotifi.identity.utils;

import com.api.hotifi.constants.TestConstants;
import com.api.hotifi.model_mocks.EmailModelMock;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class AuthenticationTestUtils {

    JSONParser jsonParser;
    FileReader fileReader;
    JSONArray jsonArray;
    JSONObject jsonObject;

    public AuthenticationTestUtils(int position) throws Exception{
        jsonParser = new JSONParser();
        fileReader = new FileReader(TestConstants.CUSTOMER_AUTHENTICATION_DATA_PATH);
        jsonArray = (JSONArray) jsonParser.parse(fileReader);
        jsonObject = (JSONObject) jsonArray.get(position);
    }

    public EmailModelMock getEmailFromJsonFile() {
        String email = (String) jsonObject.get("email");
        boolean isEmailVerified = (boolean) jsonObject.get("isEmailVerified");
        return new EmailModelMock(email, isEmailVerified);
    }

    public String getEmailJsonStringFromJsonFile() {
        jsonObject.remove("isEmailVerified");
        return jsonObject.toJSONString();
    }

}
