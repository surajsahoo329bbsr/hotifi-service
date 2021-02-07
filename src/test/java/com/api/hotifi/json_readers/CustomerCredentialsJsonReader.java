package com.api.hotifi.json_readers;

import com.api.hotifi.constants.TestConstants;
import com.api.hotifi.model_mocks.CustomerCredentialsMock;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class CustomerCredentialsJsonReader {

    JSONParser jsonParser;
    FileReader fileReader;
    JSONArray jsonArray;
    JSONObject jsonObject;

    public CustomerCredentialsJsonReader(int position) throws Exception{
        jsonParser = new JSONParser();
        fileReader = new FileReader(TestConstants.CREDENTIALS_JSON_PATH);
        jsonArray = (JSONArray) jsonParser.parse(fileReader);
        jsonObject = (JSONObject) jsonArray.get(position);
    }

    public CustomerCredentialsMock getCustomerCredentials() {
        String email = (String) jsonObject.get("email");
        String password = (String) jsonObject.get("password");
        return new CustomerCredentialsMock(email, password);
    }

}
