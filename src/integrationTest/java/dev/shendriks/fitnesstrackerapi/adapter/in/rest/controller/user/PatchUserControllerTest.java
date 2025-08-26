package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.user;

import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PatchUserControllerTest {
    private final MockMvc mvc;
    private final UserRepository userRepository;
    private final AccessTokenHelper accessTokenHelper;
    private final PasswordEncoder passwordEncoder;

    public PatchUserControllerTest(
        @Autowired MockMvc mvc,
        @Autowired UserRepository userRepository,
        @Autowired AccessTokenHelper accessTokenHelper,
        @Autowired PasswordEncoder passwordEncoder
    ) {
        this.mvc = mvc;
        this.userRepository = userRepository;
        this.accessTokenHelper = accessTokenHelper;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    public void updateUser_updatesUser() throws Exception {
        String userUlid = "USER0000000000000000000000";
        String accessToken = accessTokenHelper.getAccessToken(userUlid);

        mvc
            .perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .characterEncoding("UTF-8")
                .content("""
                    {
                        "name": "Jane Doe",
                        "email": "jane@doe.baz",
                        "currentPassword": "Sup3rS3cr3tPa$$w0rd!",
                        "newPassword": "An0th3rP4$$w0rd*",
                        "accountType": "basic"
                    }
                    """))
            .andExpect(status().isOk())
            .andDo(mvcResult -> {
                Optional<UserDbEntity> user = userRepository.findByUlid(userUlid);
                assertTrue(user.isPresent(), "Expected user to be present in database for ULID " + userUlid);
                assertInstanceOf(UserDbEntity.class, user.get());
                assertEquals("Jane Doe", user.get().getName());
                assertEquals("jane@doe.baz", user.get().getEmail());
                assertTrue(passwordEncoder.matches("An0th3rP4$$w0rd*", user.get().getPassword()));
                assertEquals("ROLE_USER", user.get().getAuthority());
            });
    }

    @Test
    public void updateUser_withEmailAlreadyRegistered_returnsError() throws Exception {
        String userUlid = "USER0000000000000000000000";
        String accessToken = accessTokenHelper.getAccessToken(userUlid);

        userRepository.save(UserDbEntity
            .builder()
            .name("Someone else")
            .email("another-foo@bar.baz")
            .password("some-hashed-password")
            .accountType(AccountType.BASIC)
            .authority("ROLE_USER")
            .build()
        );

        mvc
            .perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .characterEncoding("UTF-8")
                .content("""
                    {
                        "name": "John Doe",
                        "email": "another-foo@bar.baz",
                        "currentPassword": "Sup3rS3cr3tPa$$w0rd!",
                        "newPassword": "An0th3rP4$$w0rd*",
                        "accountType": "basic"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(content().json("""
                {
                    "message": "Email already registered",
                    "errors": ["Email already registered"]
                }
                """));
    }
}