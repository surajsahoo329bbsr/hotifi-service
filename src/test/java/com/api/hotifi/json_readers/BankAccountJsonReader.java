package com.api.hotifi.json_readers;

import com.api.hotifi.constants.TestConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class BankAccountJsonReader {

    JSONParser jsonParser;
    FileReader fileReader;
    JSONArray jsonArray;
    JSONObject jsonObject;

    public BankAccountJsonReader(int position) throws Exception {
        jsonParser = new JSONParser();
        fileReader = new FileReader(TestConstants.BANK_ACCOUNT_JSON_PATH);
        jsonArray = (JSONArray) jsonParser.parse(fileReader);
        jsonObject = (JSONObject) jsonArray.get(position);
    }

    public String getBankAccountJsonFromJsonFile() {
        return jsonObject.toJSONString();
    }

    public Long getBankAccountUserIdFromJsonFile() {
        return (Long) jsonObject.get("userId");
    }

}
