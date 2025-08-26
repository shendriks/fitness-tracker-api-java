package dev.shendriks.fitnesstrackerapi.adapter.in.rest.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.BasicAuthHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccessTokenControllerTest {
    private final BasicAuthHelper basicAuthHelper;
    private final AccessTokenHelper accessTokenHelper;
    private final MockMvc mvc;

    public AccessTokenControllerTest(
        @Autowired BasicAuthHelper basicAuthHelper,
        @Autowired AccessTokenHelper accessTokenHelper,
        @Autowired MockMvc mvc
    ) {
        this.basicAuthHelper = basicAuthHelper;
        this.accessTokenHelper = accessTokenHelper;
        this.mvc = mvc;
    }

    @Test
    void getAccessToken_withValidCredentials_returnsAccessToken() throws Exception {
        String email = "foo@bar.baz";
        String password = "Sup3rS3cr3tPa$$w0rd!";
        String basicAuthHeader = basicAuthHelper.createBasicAuthHeader(email, password);

        String accessTokenJson = mvc
            .perform(get("/api/access-token").header("Authorization", "Basic " + basicAuthHeader))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> tokenMap = mapper.readValue(accessTokenJson, new TypeReference<>() {
        });

        String actualAccessToken = (String) tokenMap.get("token");

        assertDoesNotThrow(() -> accessTokenHelper.validateAccessToken(actualAccessToken));
    }
}
