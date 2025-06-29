package dev.shendriks.fitnesstrackerapi.domain.developer.controller;

import dev.shendriks.fitnesstrackerapi.domain.developer.entity.Developer;
import dev.shendriks.fitnesstrackerapi.domain.developer.repository.DeveloperRepository;
import dev.shendriks.fitnesstrackerapi.util.BasicAuthHelper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class DeveloperControllerTest {
    @Autowired
    private BasicAuthHelper basicAuthHelper;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private DeveloperRepository developerRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
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
    public void registerDeveloperReturnsErrorIfEmailIsAlreadyRegistered() throws Exception {
        mvc
            .perform(post("/api/developers/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("""
                    {
                        "email": "foo@bar.baz",
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
    public void getDeveloperReturnsDeveloper() throws Exception {
        String developerId = "DEV00000000000000000000000";
        String email = "foo@bar.baz";
        String password = "Sup3rS3cr3tPa$$w0rd!";

        String basicAuthHeader = basicAuthHelper.createBasicAuthHeader(email, password);

        mvc.perform(get("/api/developers/" + developerId)
                .header("Authorization", "Basic " + basicAuthHeader))
            .andExpect(status().isOk())
            .andExpect(content().json("""
                    {
                        "id": "%s",
                        "email": "%s",
                        "applications": [
                        {
                            "id": "APP00000000000000000000001",
                            "name": "My Premium App",
                            "description": "My shiny new application",
                            "category": "premium",
                            "apiKey": "api-key-2"
                        },
                        {
                            "id": "APP00000000000000000000000",
                            "name": "My Basic App",
                            "description": "My shiny new application",
                            "category": "basic",
                            "apiKey": "api-key-1"
                        }
                    ]
                }
                """.formatted(developerId, email)
            ));
    }
}