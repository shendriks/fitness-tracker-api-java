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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetActivityCountControllerTest {
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final ActivityDbEntityRepository activityRepository;

    public GetActivityCountControllerTest(
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
    void getActivityCount_whenUserHasActivities_returns200_withCorrectCount() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();

        activityRepository.save(ActivityDbEntity.builder()
            .user(user)
            .activityType(ActivityType.RUNNING)
            .duration(100)
            .calories(50)
            .title("a1")
            .description("d1")
            .distance(1000)
            .startDate(Instant.parse("2025-08-21T06:00:00Z"))
            .build());
        activityRepository.save(ActivityDbEntity.builder()
            .user(user)
            .activityType(ActivityType.CYCLING)
            .duration(200)
            .calories(60)
            .title("a2")
            .description("d2")
            .distance(2000)
            .startDate(Instant.parse("2025-08-22T06:00:00Z"))
            .build());
        activityRepository.save(ActivityDbEntity.builder()
            .user(user)
            .activityType(ActivityType.WALKING)
            .duration(300)
            .calories(70)
            .title("a3")
            .description("d3")
            .distance(3000)
            .startDate(Instant.parse("2025-08-23T06:00:00Z"))
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);
        String json = mvc.perform(get("/api/activities/count")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = mapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(3, ((Number) response.get("count")).intValue(), "Expected three activities");
    }

    @Test
    void getActivityCount_whenOtherUsersActivitiesExist_doesNotCountThem() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();
        activityRepository.save(ActivityDbEntity.builder()
            .user(user)
            .activityType(ActivityType.RUNNING)
            .duration(180)
            .calories(120)
            .title("mine")
            .description("only one")
            .distance(4000)
            .startDate(Instant.parse("2025-08-21T07:00:00Z"))
            .build());

        UserDbEntity otherUser = userRepository.saveAndFlush(UserDbEntity.builder()
            .name("Alice")
            .email("alice@example.com")
            .password("password")
            .authority("ROLE_USER")
            .accountType(AccountType.BASIC)
            .build());
        activityRepository.save(ActivityDbEntity.builder()
            .user(otherUser)
            .activityType(ActivityType.CYCLING)
            .duration(120)
            .calories(80)
            .title("o1")
            .description("x")
            .distance(5000)
            .startDate(Instant.parse("2025-08-24T12:00:00Z"))
            .build());
        activityRepository.save(ActivityDbEntity.builder()
            .user(otherUser)
            .activityType(ActivityType.RUNNING)
            .duration(220)
            .calories(180)
            .title("o2")
            .description("y")
            .distance(7000)
            .startDate(Instant.parse("2025-08-25T12:00:00Z"))
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);
        String json = mvc.perform(get("/api/activities/count")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = mapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(1, ((Number) response.get("count")).intValue(), "Expected only one activity");
    }

    @Test
    void getActivityCount_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/activities/count")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}