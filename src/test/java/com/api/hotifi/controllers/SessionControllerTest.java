package com.api.hotifi.controllers;

import com.api.hotifi.json_readers.CustomerCredentialsJsonReader;
import com.api.hotifi.json_readers.SessionJsonReader;
import com.api.hotifi.utils.AccessTokenUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @RepeatedTest(value = 5, name = "Performing add session test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should add sessions")
    @Disabled
    @Order(1)
    public void shouldAddSessions(RepetitionInfo repetitionInfo) throws Exception {

        //Get email and password for customer access
        CustomerCredentialsJsonReader credentialsJsonReader = new CustomerCredentialsJsonReader(repetitionInfo.getCurrentRepetition() - 1);

        String email = credentialsJsonReader.getCustomerCredentials().getEmail();
        String password = credentialsJsonReader.getCustomerCredentials().getPassword();
        String accessToken = AccessTokenUtils.getAccessToken(email, password, mockMvc);

        SessionJsonReader jsonReader = new SessionJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        String sessionJsonString = jsonReader.getSessionJsonFromJsonFile();

        RequestBuilder requestBuilder = post("/session/")
                .header("Authorization", "Bearer " + accessToken)
                .content(sessionJsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                //.andExpect(result -> assertNotNull(result.getResolvedException()))
                //.andExpect(result -> assertEquals("Email already exists", Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andReturn();
    }

    @Test
    @DisplayName("Should get active sessions")
    @Order(2)
    public void shouldGetActiveSessions() throws Exception {
        CustomerCredentialsJsonReader credentialsJsonReader = new CustomerCredentialsJsonReader(5);

        String email = credentialsJsonReader.getCustomerCredentials().getEmail();
        String password = credentialsJsonReader.getCustomerCredentials().getPassword();
        String accessToken = AccessTokenUtils.getAccessToken(email, password, mockMvc);

        Set<String> usernames = Set.of(
                "rocketman12",
                "freakin17",
                "jackMaFia69",
                "Reacher63",
                "methKing"
        );

        RequestBuilder requestBuilder = get("/session/active/" + String.join(",", usernames))
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andReturn();
    }
}
