package dev.shendriks.fitnesstrackerapi.domain.developer.controller;

import dev.shendriks.fitnesstrackerapi.domain.developer.entity.Developer;
import dev.shendriks.fitnesstrackerapi.domain.developer.repository.DeveloperRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class DeveloperControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DeveloperRepository developerRepository;

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
            })
        ;
    }
}