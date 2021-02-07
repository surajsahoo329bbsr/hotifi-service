package com.api.hotifi.controllers;

import com.api.hotifi.json_readers.UserJsonReader;
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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @RepeatedTest(value = 10, name = "Performing add user test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should add user")
    @Order(1) //Order 1 means this will be the first test
    public void shouldAddUser(RepetitionInfo repetitionInfo) throws Exception {
        UserJsonReader jsonReader = new UserJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        String userRequestJsonString = jsonReader.getUserRequestFromJsonFile();
        String accessToken = AccessTokenUtils.getAccessToken("suraj@gmail.com", "admin", mockMvc);
        RequestBuilder requestBuilder = post("/user/")
                .header("Authorization", "Bearer " + accessToken)
                .content(userRequestJsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNoContent())
                //.andExpect(result -> assertNotNull(result.getResolvedException()))
                //.andExpect(result -> assertEquals("Email already exists", Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andReturn();

    }

    @RepeatedTest(value = 10, name = "Performing get verified users - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should get verified users")
    @Order(2)
    public void shouldGetVerifiedAuthentication(RepetitionInfo repetitionInfo) throws Exception {
        UserJsonReader jsonReader = new UserJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        String username = jsonReader.getUsernameFromJsonFile();

        String accessToken = AccessTokenUtils.getAccessToken("suraj@gmail.com", "admin", mockMvc);
        RequestBuilder requestBuilder = get("/user/" + username)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\n" +
                                "\t\t\"username\": \"" + username + "\",\n" +
                                "\t\t\"authentication\": {\n" +
                                "\t\t\t\"activated\": true\n" +
                                "\t\t}\n" +
                                "\t}"
                ))
                .andReturn();
    }

}
