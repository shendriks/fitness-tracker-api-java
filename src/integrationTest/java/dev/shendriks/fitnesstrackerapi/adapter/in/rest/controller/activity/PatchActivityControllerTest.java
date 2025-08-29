package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PatchActivityControllerTest {
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final ActivityDbEntityRepository activityRepository;

    public PatchActivityControllerTest(
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
    void patchActivity_withOwnActivityAndValidPayload_updatesAndReturns200() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();
        ActivityDbEntity activity = activityRepository.save(ActivityDbEntity.builder()
            .user(user)
            .activityType(ActivityType.WALKING)
            .duration(Duration.ofSeconds(100))
            .distance(Distance.ofMeters(1234))
            .calories(10)
            .title("Old Title")
            .description("Old Desc")
            .startDate(Instant.parse("2025-08-20T10:00:00Z"))
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);
        String actualResponse = mvc.perform(patch("/api/activities/" + activity.getUlid())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content("""
                    {
                        "activityType": "running",
                        "title": "New Title",
                        "description": "New Desc"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = mapper.readValue(actualResponse, Map.class);
        assertEquals(activity.getUlid(), response.get("id"));
        assertEquals("running", response.get("activityType"));
        assertEquals("New Title", response.get("title"));
        assertEquals("New Desc", response.get("description"));

        ActivityDbEntity updated = activityRepository.findByUserIdAndUlid(user.getId(), activity.getUlid()).orElseThrow();
        assertEquals(ActivityType.RUNNING, updated.getActivityType());
        assertEquals("New Title", updated.getTitle());
        assertEquals("New Desc", updated.getDescription());
    }

    @Test
    void patchActivity_ofAnotherUser_returns404() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity otherUser = userRepository.saveAndFlush(UserDbEntity.builder()
            .name("Other")
            .email("other@example.com")
            .password("password")
            .authority("ROLE_USER")
            .accountType(AccountType.BASIC)
            .build());
        ActivityDbEntity othersActivity = activityRepository.save(ActivityDbEntity.builder()
            .user(otherUser)
            .activityType(ActivityType.CYCLING)
            .duration(Duration.ofSeconds(200))
            .distance(Distance.ofMeters(2000))
            .calories(20)
            .title("Other Title")
            .description("Other Desc")
            .startDate(Instant.parse("2025-08-22T10:00:00Z"))
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);
        mvc.perform(patch("/api/activities/" + othersActivity.getUlid())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content("""
                    {
                        "activityType": "running",
                        "title": "New Title",
                        "description": "New Desc"
                    }
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void patchActivity_withNonExisting_returns404() throws Exception {
        String userUlid = "USER0000000000000000000000";

        String token = accessTokenHelper.getAccessToken(userUlid);
        mvc.perform(patch("/api/activities/01JZQZZYETYBWQV7KF0BHGMBDJ")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content("""
                    {
                        "activityType": "walking",
                        "title": "Title",
                        "description": "Desc"
                    }
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void patchActivity_withoutAuth_returns401() throws Exception {
        mvc.perform(patch("/api/activities/01JZQZZYETYBWQV7KF0BHGMBDJ")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "activityType": "walking",
                        "title": "Title",
                        "description": "Desc"
                    }
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void patchActivity_withInvalidActivityType_returns400() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();
        ActivityDbEntity activity = activityRepository.save(ActivityDbEntity.builder()
            .user(user)
            .activityType(ActivityType.WALKING)
            .duration(Duration.ofSeconds(100))
            .distance(Distance.ofMeters(1234))
            .calories(10)
            .title("Old Title")
            .description("Old Desc")
            .startDate(Instant.parse("2025-08-20T10:00:00Z"))
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);
        mvc.perform(patch("/api/activities/" + activity.getUlid())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content("""
                    {
                        "activityType": "NOT_A_TYPE",
                        "title": "New Title",
                        "description": "New Desc"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }
}
