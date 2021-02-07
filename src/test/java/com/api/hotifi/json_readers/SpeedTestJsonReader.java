package com.api.hotifi.json_readers;

import com.api.hotifi.constants.TestConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class SpeedTestJsonReader {

    JSONParser jsonParser;
    FileReader fileReader;
    JSONArray jsonArray;
    JSONObject jsonObject;

    public SpeedTestJsonReader(int position) throws Exception{
        jsonParser = new JSONParser();
        fileReader = new FileReader(TestConstants.SPEED_TESTS_JSON_PATH);
        jsonArray = (JSONArray) jsonParser.parse(fileReader);
        jsonObject = (JSONObject) jsonArray.get(position);
    }

    public String getSpeedTestRequestFromJsonFile() {
        return jsonObject.toJSONString();
    }

}
