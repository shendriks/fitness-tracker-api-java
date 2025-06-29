package dev.shendriks.fitnesstrackerapi.domain.user.controller;

import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.user.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.util.BasicAuthHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {
    private final BasicAuthHelper basicAuthHelper;
    private final MockMvc mvc;
    private final UserRepository userRepository;

    public UserControllerTest(
        @Autowired BasicAuthHelper basicAuthHelper,
        @Autowired MockMvc mvc,
        @Autowired UserRepository userRepository
    ) {
        this.basicAuthHelper = basicAuthHelper;
        this.mvc = mvc;
        this.userRepository = userRepository;
    }

    @Test
    public void registerUser_registersUser() throws Exception {
        mvc
            .perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("""
                    {
                        "email": "email@example.com",
                        "password": "sup3rS3cr37Pa$$w0rd",
                        "accountType": "basic"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andDo(mvcResult -> {
                String location = mvcResult.getResponse().getHeader("Location");
                assertInstanceOf(String.class, location);

                Pattern locationPattern = Pattern.compile("^/api/users/(?<ulid>[A-Z0-9]{26})$");
                Matcher matcher = locationPattern.matcher(location);
                assertTrue(matcher.matches(), "Expected " + location + " to match " + locationPattern.pattern());

                String ulid = matcher.group("ulid");
                Optional<User> user = userRepository.findByUlid(ulid);
                assertTrue(user.isPresent(), "Expected user to be present in database for ULID " + ulid);
            });
    }

    @Test
    public void registerUserReturnsErrorIfEmailIsAlreadyRegistered() throws Exception {
        mvc
            .perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("""
                    {
                        "email": "foo@bar.baz",
                        "password": "sup3rS3cr37Pa$$w0rd",
                        "accountType": "basic"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(content().json("""
                {
                    "message": "Email already registered",
                    "errors": [
                        "Email already registered"
                    ]
                }
                """));
    }

    @Test
    public void getUserReturnsUser() throws Exception {
        String userId = "USER0000000000000000000000";
        String email = "foo@bar.baz";
        String password = "Sup3rS3cr3tPa$$w0rd!";

        String basicAuthHeader = basicAuthHelper.createBasicAuthHeader(email, password);

        mvc.perform(get("/api/users/" + userId)
                .header("Authorization", "Basic " + basicAuthHeader))
            .andExpect(status().isOk())
            .andExpect(content().json("""
                    {
                        "id": "%s",
                        "email": "%s",
                        "accountType": "basic"
                }
                """.formatted(userId, email)
            ));
    }
}