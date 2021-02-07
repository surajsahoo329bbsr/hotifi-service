package com.api.hotifi.controllers;

import com.api.hotifi.json_readers.CustomerCredentialsJsonReader;
import com.api.hotifi.json_readers.LatestSpeedTestsJsonReader;
import com.api.hotifi.json_readers.SpeedTestJsonReader;
import com.api.hotifi.utils.AccessTokenUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @RepeatedTest(value = 50, name = "Performing add speed test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should add speed tests")
    @Order(1)
    public void shouldAddSpeedTests(RepetitionInfo repetitionInfo) throws Exception {

        //Get email and password for customer access
        CustomerCredentialsJsonReader credentialsJsonReader = new CustomerCredentialsJsonReader((repetitionInfo.getCurrentRepetition() - 1) / 5);

        String email = credentialsJsonReader.getCustomerCredentials().getEmail();
        String password = credentialsJsonReader.getCustomerCredentials().getPassword();
        String accessToken = AccessTokenUtils.getAccessToken(email, password, mockMvc);

        SpeedTestJsonReader jsonReader = new SpeedTestJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        String speedTestJsonString = jsonReader.getSpeedTestRequestFromJsonFile();

        RequestBuilder requestBuilder = post("/speed-test/")
                .header("Authorization", "Bearer " + accessToken)
                .content(speedTestJsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNoContent())
                //.andExpect(result -> assertNotNull(result.getResolvedException()))
                //.andExpect(result -> assertEquals("Email already exists", Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andReturn();
    }

    //For 10 users value = 10
    @RepeatedTest(value = 10, name = "Performing get latest speed test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should get latest speed test")
    @Order(2)
    public void shouldGetLatestSpeedTest(RepetitionInfo repetitionInfo) throws Exception {
        CustomerCredentialsJsonReader credentialsJsonReader = new CustomerCredentialsJsonReader(repetitionInfo.getCurrentRepetition() - 1);

        String email = credentialsJsonReader.getCustomerCredentials().getEmail();
        String password = credentialsJsonReader.getCustomerCredentials().getPassword();
        String accessToken = AccessTokenUtils.getAccessToken(email, password, mockMvc);

        LatestSpeedTestsJsonReader jsonReader = new LatestSpeedTestsJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        Long userId = jsonReader.getLatestSpeedTestFromJsonFile().getUserId();
        String pinCode = jsonReader.getLatestSpeedTestFromJsonFile().getPinCode();
        boolean isWifi = jsonReader.getLatestSpeedTestFromJsonFile().isWifi();

        RequestBuilder requestBuilder = get("/speed-test/" + userId + "/" + pinCode + "/" + isWifi)
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
