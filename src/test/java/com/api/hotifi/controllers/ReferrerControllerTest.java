package com.api.hotifi.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReferrerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @RepeatedTest(value = 99999, name = "Performing verify referral code - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should verify referral code")
    @Order(1)
    public void shouldHaveUserIdInReferralCode(RepetitionInfo repetitionInfo) throws Exception {
        int userId = repetitionInfo.getCurrentRepetition();
        RequestBuilder requestBuilder = get("/referrer/" + userId);
        mockMvc
                .perform(requestBuilder)
                .andExpect(content().string("true"))
                .andReturn();
    }


}
