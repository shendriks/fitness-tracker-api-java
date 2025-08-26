package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ActivityDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetActivitiesControllerTest {
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final ActivityDbEntityRepository activityRepository;

    public GetActivitiesControllerTest(
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
    void getActivities_returnsAllForUser_inDescendingUlidOrder() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();

        ActivityDbEntity a1 = activityRepository.save(ActivityDbEntity.builder()
            .user(user)
            .activityType(ActivityType.RUNNING)
            .duration(1800)
            .calories(500)
            .title("Morning Run")
            .description("Nice run")
            .distance(10000)
            .startDate(Instant.parse("2025-08-21T06:00:00Z"))
            .build());
        ActivityDbEntity a2 = activityRepository.save(ActivityDbEntity.builder()
            .user(user)
            .activityType(ActivityType.CYCLING)
            .duration(3600)
            .calories(800)
            .title("Evening Ride")
            .description("Chill ride")
            .distance(25000)
            .startDate(Instant.parse("2025-08-22T18:30:00Z"))
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);

        String json = mvc.perform(get("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> list = mapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(2, list.size(), "Expected two activities for the user");
        assertEquals(a1.getUlid(), list.get(1).get("id"));
        assertEquals(a2.getUlid(), list.get(0).get("id"));
        String firstId = (String) list.get(0).get("id");
        String secondId = (String) list.get(1).get("id");
        assertTrue(firstId.compareTo(secondId) > 0, "Expected ULIDs to be ordered descending");
    }

    @Test
    void getActivities_excludesActivitiesOfAnotherUser() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();

        ActivityDbEntity activity = activityRepository.save(ActivityDbEntity.builder()
            .user(user)
            .activityType(ActivityType.RUNNING)
            .duration(1200)
            .calories(300)
            .title("Seeded User Activity")
            .description("Run")
            .distance(3000)
            .startDate(Instant.parse("2025-08-22T07:00:00Z"))
            .build());

        UserDbEntity otherUser = UserDbEntity.builder()
            .name("Bob")
            .email("bob@example.com")
            .password("password")
            .authority("ROLE_USER")
            .accountType(AccountType.BASIC)
            .build();
        otherUser = userRepository.saveAndFlush(otherUser);
        activityRepository.save(ActivityDbEntity.builder()
            .user(otherUser)
            .activityType(ActivityType.WALKING)
            .duration(600)
            .calories(100)
            .title("Other User Activity")
            .description("Walk")
            .distance(1000)
            .startDate(Instant.parse("2025-08-22T08:00:00Z"))
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);

        String json = mvc.perform(get("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> list = mapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(1, list.size(), "Expected only the authenticated user's activity to be returned");
        assertEquals(activity.getUlid(), list.getFirst().get("id"));
        assertEquals("Seeded User Activity", list.getFirst().get("title"));
    }
}
