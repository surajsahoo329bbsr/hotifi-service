package com.api.hotifi.controllers;

import com.api.hotifi.json_readers.CustomerCredentialsJsonReader;
import com.api.hotifi.json_readers.LatestSpeedTestsJsonReader;
import com.api.hotifi.json_readers.PurchaseJsonReader;
import com.api.hotifi.utils.AccessTokenUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @RepeatedTest(value = 5, name = "Performing check purchase test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should check purchase before adding")
    @Order(1)
    public void shouldCheckPurchaseBeforeAdding(RepetitionInfo repetitionInfo) throws Exception {

        /*CustomerCredentialsJsonReader credentialsJsonReader = new CustomerCredentialsJsonReader((repetitionInfo.getCurrentRepetition() - 1) / 5);

        String email = credentialsJsonReader.getCustomerCredentials().getEmail();
        String password = credentialsJsonReader.getCustomerCredentials().getPassword();*/
        //below credentials have been updated on - 17/09/21 for beta testing
        String accessToken = AccessTokenUtils.getAccessToken("suraj.admin@hotifi", "admin", mockMvc);

        PurchaseJsonReader jsonReader = new PurchaseJsonReader(repetitionInfo.getCurrentRepetition() - 1);

        Long buyerId = jsonReader.getPurchaseCheckFromJsonFile().getBuyerId();
        Long sessionId = jsonReader.getPurchaseCheckFromJsonFile().getSessionId();
        Long dataToBeUsed = jsonReader.getPurchaseCheckFromJsonFile().getDataToBeUsed();

        RequestBuilder requestBuilder = get("/purchase/buyer/check-current-session/" + buyerId + "/" + sessionId + "/" + dataToBeUsed)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
    }


    @RepeatedTest(value = 5, name = "Performing add purchase test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should add purchase")
    @Order(2)
    public void shouldAddPurchases(RepetitionInfo repetitionInfo) throws Exception {

        //Get email and password for customer access
        CustomerCredentialsJsonReader credentialsJsonReader = new CustomerCredentialsJsonReader((repetitionInfo.getCurrentRepetition() - 1) / 5);

        String email = credentialsJsonReader.getCustomerCredentials().getEmail();
        String password = credentialsJsonReader.getCustomerCredentials().getPassword();
        String accessToken = AccessTokenUtils.getAccessToken(email, password, mockMvc);

        PurchaseJsonReader jsonReader = new PurchaseJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        String purchaseJsonString = jsonReader.getPurchaseJsonFromJsonFile();

        RequestBuilder requestBuilder = post("/purchase/buyer")
                .header("Authorization", "Bearer " + accessToken)
                .content(purchaseJsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                //.andExpect(result -> assertNotNull(result.getResolvedException()))
                //.andExpect(result -> assertEquals("Email already exists", Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andReturn();
    }

    //For 5 users value = 5
    @RepeatedTest(value = 5, name = "Performing get latest speed test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should get latest speed test")
    @Order(3)
    @Disabled //Don't comment this before testing all purchase use-cases
    public void shouldGetPurchaseReceipt(RepetitionInfo repetitionInfo) throws Exception {
        CustomerCredentialsJsonReader credentialsJsonReader = new CustomerCredentialsJsonReader(repetitionInfo.getCurrentRepetition() - 1);

        String email = credentialsJsonReader.getCustomerCredentials().getEmail();
        String password = credentialsJsonReader.getCustomerCredentials().getPassword();
        String accessToken = AccessTokenUtils.getAccessToken(email, password, mockMvc);

        LatestSpeedTestsJsonReader jsonReader = new LatestSpeedTestsJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        Long userId = jsonReader.getLatestSpeedTestFromJsonFile().getUserId();
        String pinCode = jsonReader.getLatestSpeedTestFromJsonFile().getPinCode();
        boolean isWifi = jsonReader.getLatestSpeedTestFromJsonFile().isWifi();

        RequestBuilder requestBuilder = get("/purchase/" + userId + "/" + pinCode + "/" + isWifi)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                //.andExpect(result -> assertNotNull(result.getResolvedException()))
                //.andExpect(result -> assertEquals("Email already exists", Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andReturn();

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\n" +
                                "    \"pinCode\": \"" + pinCode + "\",\n" +
                                "  }"
                ))
                .andReturn();
    }

}
