package com.api.hotifi.controllers;

import com.api.hotifi.json_readers.BankAccountJsonReader;
import com.api.hotifi.json_readers.CustomerCredentialsJsonReader;
import com.api.hotifi.utils.AccessTokenUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @RepeatedTest(value = 5, name = "Performing add bank account test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should add bank accounts test")
    @Order(1)
    public void shouldAddBankAccountByCustomer(RepetitionInfo repetitionInfo) throws Exception {

        //Get email and password for customer access
        CustomerCredentialsJsonReader credentialsJsonReader = new CustomerCredentialsJsonReader((repetitionInfo.getCurrentRepetition() - 1) / 5);

        String email = credentialsJsonReader.getCustomerCredentials().getEmail();
        String password = credentialsJsonReader.getCustomerCredentials().getPassword();
        String accessToken = AccessTokenUtils.getAccessToken(email, password, mockMvc);

        BankAccountJsonReader jsonReader = new BankAccountJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        String bankAccountJsonString = jsonReader.getBankAccountJsonFromJsonFile();

        RequestBuilder requestBuilder = post("/bank-account/seller")
                .header("Authorization", "Bearer " + accessToken)
                .content(bankAccountJsonString)
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
    @RepeatedTest(value = 5, name = "Performing update successful linked accounts test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should update successful linked accounts test")
    @Order(2)
    public void shouldUpdateSuccessfulLinkedAccounts(RepetitionInfo repetitionInfo) throws Exception {

        String accessToken = AccessTokenUtils.getAccessToken("suraj@gmail.com", "admin", mockMvc);

        BankAccountJsonReader jsonReader = new BankAccountJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        Long userId = jsonReader.getBankAccountUserIdFromJsonFile();
        String linkedAccountId = "lnk_" + userId;

        RequestBuilder requestBuilder = put("/bank-account/admin/update/success/" + userId + "/" + linkedAccountId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNoContent())
                //.andExpect(result -> assertNotNull(result.getResolvedException()))
                //.andExpect(result -> assertEquals("Email already exists", Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andReturn();
    }


}
