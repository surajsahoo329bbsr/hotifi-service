package com.api.hotifi.json_readers;

import com.api.hotifi.constants.TestConstants;
import com.api.hotifi.model_mocks.PurchaseCheckMock;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class PurchaseJsonReader {

    JSONParser jsonParser;
    FileReader fileReader;
    JSONArray jsonArray;
    JSONObject jsonObject;

    public PurchaseJsonReader(int position) throws Exception {
        jsonParser = new JSONParser();
        fileReader = new FileReader(TestConstants.PURCHASE_JSON_PATH);
        jsonArray = (JSONArray) jsonParser.parse(fileReader);
        jsonObject = (JSONObject) jsonArray.get(position);
    }

    public String getPurchaseJsonFromJsonFile() {
        return jsonObject.toJSONString();
    }

    public PurchaseCheckMock getPurchaseCheckFromJsonFile(){
        Long buyerId = (Long) jsonObject.get("buyerId");
        Long sessionId = (Long) jsonObject.get("sessionId");
        Long dataToBeUsed = (Long) jsonObject.get("data");
        return new PurchaseCheckMock(buyerId, sessionId, dataToBeUsed);
    }

}
