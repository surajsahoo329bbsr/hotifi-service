package com.api.hotifi.controllers;

import com.api.hotifi.json_readers.AuthenticationJsonReader;
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

//@DataJpaTest //Uncomment this for Junit5 tests, and after uncommenting this comment SpringBootTest
//@RunWith(SpringRunner.class) //Uncomment this to remove runnable methods check
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //Required for order
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @RepeatedTest(value = 10, name = "Performing add email test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should add email")
    @Order(1) //Order 1 means this will be the first test
    public void shouldAddEmail(RepetitionInfo repetitionInfo) throws Exception {
        AuthenticationJsonReader jsonReader = new AuthenticationJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        String email = jsonReader.getEmailModelMockFromJsonFile().getEmail();
        boolean isVerified = jsonReader.getEmailModelMockFromJsonFile().isVerified();
        //below credentials have been updated on - 17/09/21 for beta testing
        String accessToken = AccessTokenUtils.getAccessToken("suraj.admin@hotifi", "admin", mockMvc);
        RequestBuilder requestBuilder = post("/authenticate/sign-up/" + email + "/" + isVerified)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                //.andExpect(result -> assertNotNull(result.getResolvedException()))
                //.andExpect(result -> assertEquals("Email already exists", Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andReturn();

    }

    @RepeatedTest(value = 10, name = "Performing phone verification test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should verify phones")
    @Order(2)
    public void shouldVerifyPhone(RepetitionInfo repetitionInfo) throws Exception {
        AuthenticationJsonReader jsonReader = new AuthenticationJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        String jsonRequest = jsonReader.getEmailJsonFromJsonFile();
        //below credentials have been updated on - 17/09/21 for beta testing
        String accessToken = AccessTokenUtils.getAccessToken("suraj.admin@hotifi", "admin", mockMvc);

        mockMvc
                .perform(put("/authenticate/sign-up/verify/phone")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json;charset=UTF-8")
                        .content(jsonRequest)
                        .accept("application/json;charset=UTF-8"))
                .andExpect(status().isNoContent());
    }

    @RepeatedTest(value = 10, name = "Performing get authentication test - {currentRepetition}/{totalRepetitions} ...")
    @DisplayName("Should get authentication")
    @Order(3)
    public void shouldGetAuthentication(RepetitionInfo repetitionInfo) throws Exception {
        AuthenticationJsonReader jsonReader = new AuthenticationJsonReader(repetitionInfo.getCurrentRepetition() - 1);
        String email = jsonReader.getEmailModelMockFromJsonFile().getEmail();
        //below credentials have been updated on - 17/09/21 for beta testing
        String accessToken = AccessTokenUtils.getAccessToken("suraj.admin@hotifi", "admin", mockMvc);
        RequestBuilder requestBuilder = get("/authenticate/" + email)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{ email : " + email + " }"
                ))
                .andReturn();
    }

}
