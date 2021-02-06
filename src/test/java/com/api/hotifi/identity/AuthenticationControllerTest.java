package com.api.hotifi.identity;

import com.api.hotifi.identity.utils.AuthenticationTestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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

    public String getAccessToken(String username, String password) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "client");
        params.add("username", username);
        params.add("password", password);

        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic("client", "secret"))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    @RepeatedTest(10)
    @Order(1) //Order 1 means this will be the first test
    public void shouldAddEmail(RepetitionInfo repetitionInfo) throws Exception {
        AuthenticationTestUtils utils = new AuthenticationTestUtils(repetitionInfo.getCurrentRepetition() - 1);
        String email = utils.getEmailFromJsonFile().getEmail();//addEmailMocks.get(repetitionInfo.getCurrentRepetition() - 1).getEmail();
        boolean isVerified = utils.getEmailFromJsonFile().isVerified();//addEmailMocks.get(repetitionInfo.getCurrentRepetition() - 1).isVerified();
        String accessToken = getAccessToken("suraj@gmail.com", "admin");
        RequestBuilder requestBuilder = post("/authenticate/sign-up/" + email + "/" + isVerified)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
    }

    @RepeatedTest(10)
    @Order(2)
    public void shouldVerifyPhone(RepetitionInfo repetitionInfo) throws Exception {
        AuthenticationTestUtils utils = new AuthenticationTestUtils(repetitionInfo.getCurrentRepetition() - 1);
        String jsonRequest = utils.getEmailJsonStringFromJsonFile();
        String accessToken = getAccessToken("suraj@gmail.com", "admin");

        mockMvc
                .perform(put("/authenticate/sign-up/verify/phone")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json;charset=UTF-8")
                        .content(jsonRequest)
                        .accept("application/json;charset=UTF-8"))
                .andExpect(status().isNoContent());
    }

    @RepeatedTest(10)
    @Order(3)
    public void shouldGetAuthentication(RepetitionInfo repetitionInfo) throws Exception {
        AuthenticationTestUtils utils = new AuthenticationTestUtils(repetitionInfo.getCurrentRepetition() - 1);
        String email = utils.getEmailFromJsonFile().getEmail();

        String accessToken = getAccessToken("suraj@gmail.com", "admin");
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
