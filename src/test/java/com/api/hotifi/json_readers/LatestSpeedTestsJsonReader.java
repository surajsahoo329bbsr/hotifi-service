package com.api.hotifi.json_readers;

import com.api.hotifi.constants.TestConstants;
import com.api.hotifi.model_mocks.LatestSpeedTestMock;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class LatestSpeedTestsJsonReader {

    JSONParser jsonParser;
    FileReader fileReader;
    JSONArray jsonArray;
    JSONObject jsonObject;

    public LatestSpeedTestsJsonReader(int position) throws Exception{
        jsonParser = new JSONParser();
        fileReader = new FileReader(TestConstants.LATEST_SPEED_TEST_JSON_PATH);
        jsonArray = (JSONArray) jsonParser.parse(fileReader);
        jsonObject = (JSONObject) jsonArray.get(position);
    }

    public LatestSpeedTestMock getLatestSpeedTestFromJsonFile() {
        Long userId = (Long) jsonObject.get("userId");
        String pinCode = (String) jsonObject.get("pinCode");
        boolean isWifi = (boolean) jsonObject.get("isWifi");
        return new LatestSpeedTestMock(userId, pinCode, isWifi);
    }
}
