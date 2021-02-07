package com.api.hotifi.json_readers;

import com.api.hotifi.constants.TestConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class UserJsonReader {

    JSONParser jsonParser;
    FileReader fileReader;
    JSONArray jsonArray;
    JSONObject jsonObject;

    public UserJsonReader(int position) throws Exception{
        jsonParser = new JSONParser();
        fileReader = new FileReader(TestConstants.CUSTOMER_DETAILS_JSON_PATH);
        jsonArray = (JSONArray) jsonParser.parse(fileReader);
        jsonObject = (JSONObject) jsonArray.get(position);
    }

    public String getUserRequestFromJsonFile() {
        return jsonObject.toJSONString();
    }

    public String getUsernameFromJsonFile(){
        return (String) jsonObject.get("username");
    }

}
