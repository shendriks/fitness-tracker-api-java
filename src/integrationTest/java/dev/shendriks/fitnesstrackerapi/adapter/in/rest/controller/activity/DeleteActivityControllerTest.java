package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ActivityDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeleteActivityControllerTest {
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final ActivityDbEntityRepository activityRepository;

    public DeleteActivityControllerTest(
        @Autowired MockMvc mvc,
        @Autowired AccessTokenHelper accessTokenHelper,
        @Autowired UserRepository userRepository,
        @Autowired ActivityDbEntityRepository activityRepository
    ) {
        this.mvc = mvc;
        this.accessTokenHelper = accessTokenHelper;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    @Test
    void deleteActivity_withOwnActivity_returns204andIsRemoved() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();
        ActivityDbEntity activity = activityRepository.save(ActivityDbEntity
            .builder()
            .user(user)
            .activityType(ActivityType.RUNNING)
            .duration(Duration.ofSeconds(1800.0))
            .distance(Distance.ofMeters(5000.0))
            .calories(500)
            .title("My Activity")
            .description("Test activity")
            .startDate(Instant.parse("2025-08-22T06:00:00Z"))
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);
        mvc.perform(delete("/api/activities/" + activity.getUlid())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());

        assertTrue(activityRepository.findById(activity.getId()).isEmpty(), "Activity should be deleted for the user");
    }

    @Test
    void deleteActivity_withNonExisting_returns404() throws Exception {
        String userUlid = "USER0000000000000000000000";
        String token = accessTokenHelper.getAccessToken(userUlid);
        String nonExistingUlid = "TESTULID000000000000004711";

        mvc.perform(delete("/api/activities/" + nonExistingUlid)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteActivity_ofAnotherUser_returns404() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity otherUser = UserDbEntity
            .builder()
            .name("Bob")
            .email("bob@example.com")
            .password("password")
            .authority("ROLE_USER")
            .accountType(AccountType.BASIC)
            .build();

        otherUser = userRepository.saveAndFlush(otherUser);
        ActivityDbEntity activity = activityRepository.save(ActivityDbEntity
            .builder()
            .user(otherUser)
            .activityType(ActivityType.RUNNING)
            .duration(Duration.ofSeconds(1800.0))
            .distance(Distance.ofMeters(5000.0))
            .calories(500)
            .title("My Activity")
            .description("Test activity")
            .startDate(Instant.parse("2025-08-22T06:00:00Z"))
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);
        mvc.perform(delete("/api/activities/" + activity.getUlid())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }
}
