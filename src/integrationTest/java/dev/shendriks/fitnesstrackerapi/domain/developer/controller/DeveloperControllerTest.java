package dev.shendriks.fitnesstrackerapi.domain.developer.controller;

import dev.shendriks.fitnesstrackerapi.domain.developer.entity.Developer;
import dev.shendriks.fitnesstrackerapi.domain.developer.repository.DeveloperRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class DeveloperControllerTest {
    private static final String TEST_EMAIL = "email@example.com";
    private static final String TEST_PASSWORD = "12345";
    private static final String DEVELOPER_ROLE = "ROLE_DEVELOPER";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private DeveloperRepository developerRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    public void registerDeveloper_registersDeveloper() throws Exception {
        mvc
            .perform(post("/api/developers/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("""
                    {
                        "email": "email@example.com",
                        "password": "sup3rS3cr37Pa$$w0rd"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andDo(mvcResult -> {
                String location = mvcResult.getResponse().getHeader("Location");
                assertInstanceOf(String.class, location);

                Pattern locationPattern = Pattern.compile("^/api/developers/(?<ulid>[A-Z0-9]{26})$");
                Matcher matcher = locationPattern.matcher(location);
                assertTrue(matcher.matches(), "Expected " + location + " to match " + locationPattern.pattern());

                String ulid = matcher.group("ulid");
                Optional<Developer> developer = developerRepository.findByUlid(ulid);
                assertTrue(developer.isPresent(), "Expected developer to be present in database for ULID " + ulid);
            });
    }

    @Test
    @Transactional
    public void registerDeveloperReturnsErrorIfEmailIsAlreadyRegistered() throws Exception {
        createDeveloper(TEST_EMAIL, TEST_PASSWORD);

        mvc
            .perform(post("/api/developers/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("""
                    {
                        "email": "email@example.com",
                        "password": "sup3rS3cr37Pa$$w0rd"
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
    @Transactional
    public void getDeveloperReturnsDeveloper() throws Exception {
        Developer developer = createDeveloper(TEST_EMAIL, TEST_PASSWORD);
        String developerId = developer.getUlid();
        String basicAuthHeader = createBasicAuthHeader(TEST_EMAIL, TEST_PASSWORD);

        mvc.perform(get("/api/developers/" + developerId)
                .header("Authorization", "Basic " + basicAuthHeader))
            .andExpect(status().isOk())
            .andExpect(content().json("""
                {
                    "id": "%s",
                    "email": "%s",
                    "applications": []
                }
                """.formatted(developerId, TEST_EMAIL)
            ));
    }

    private Developer createDeveloper(String email, String password) {
        Developer developer = Developer.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .authority(DEVELOPER_ROLE)
            .build();

        developer = developerRepository.save(developer);
        entityManager.flush();

        return developer;
    }

    private String createBasicAuthHeader(String email, String password) {
        String credentials = email + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}