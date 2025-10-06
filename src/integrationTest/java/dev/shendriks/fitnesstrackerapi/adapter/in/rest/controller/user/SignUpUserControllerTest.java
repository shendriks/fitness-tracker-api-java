package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.user;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignUpUserControllerTest {
    private final MockMvc mvc;
    private final UserRepository userRepository;

    public SignUpUserControllerTest(
        @Autowired MockMvc mvc,
        @Autowired UserRepository userRepository
    ) {
        this.mvc = mvc;
        this.userRepository = userRepository;
    }

    @Test
    public void registerUser_withValidPayload_registersUser() throws Exception {
        mvc
            .perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("""
                    {
                        "email": "email@example.com",
                        "password": "sup3rS3cr37Pa$$w0rd",
                        "accountType": "basic",
                        "name": "John Doe"
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
                Optional<UserDbEntity> user = userRepository.findByUlid(ulid);
                assertTrue(user.isPresent(), "Expected user to be present in database for ULID " + ulid);
            });
    }

    @Test
    public void registerUser_withEmailAlreadyRegistered_returns400() throws Exception {
        mvc
            .perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("""
                    {
                        "email": "foo@bar.baz",
                        "password": "sup3rS3cr37Pa$$w0rd",
                        "accountType": "basic",
                        "name": "John Doe"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(content().json("""
                {
                    "message": "An error occurred",
                    "errors": ["Email already registered"]
                }
                """));
    }
}