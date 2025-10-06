package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.user;

import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetCurrentUserControllerTest {
    private final AccessTokenHelper accessTokenHelper;
    private final MockMvc mvc;

    public GetCurrentUserControllerTest(
        @Autowired AccessTokenHelper accessTokenHelper,
        @Autowired MockMvc mvc
    ) {
        this.accessTokenHelper = accessTokenHelper;
        this.mvc = mvc;
    }

    @Test
    public void getUser_returnsUser() throws Exception {
        String userUlid = "USER0000000000000000000000";
        String email = "foo@bar.baz";
        String accessToken = accessTokenHelper.getAccessToken(userUlid);

        mvc
            .perform(get("/api/users/me").header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(content().json("""
                {
                    "id": "%s",
                    "email": "%s",
                    "accountType": "basic"
                }
                """.formatted(userUlid, email)
            ));
    }

    @Test
    public void getUser_withInvalidToken_returns401() throws Exception {
        String accessToken = "some-invalid-token";

        mvc
            .perform(get("/api/users/me").header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json("""
                {
                  "message": "An error occurred",
                  "errors": ["Invalid access token"]
                }
                """
            ));
    }
}