package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ActivityDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.infrastructure.Constant;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostActivityControllerTest {
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final ActivityDbEntityRepository activityRepository;

    public PostActivityControllerTest(
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
    void postActivity_withValidPayload_persistsAndReturns200() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();

        String token = accessTokenHelper.getAccessToken(userUlid);
        String json = mvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content("""
                    {
                        "activityType": "running",
                        "duration": 3600,
                        "calories": 900,
                        "title": "Morning Run",
                        "description": "Nice run",
                        "distance": 10000,
                        "startDate": "2025-08-21T06:00:00Z"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = mapper.readValue(json, new TypeReference<>() {
        });
        assertNotNull(response.get("id"));
        assertEquals("running", response.get("activityType"));
        assertEquals(3600, ((Number) response.get("duration")).intValue());
        assertEquals(900, ((Number) response.get("calories")).intValue());
        assertEquals("Morning Run", response.get("title"));
        assertEquals("Nice run", response.get("description"));
        assertEquals(10000, ((Number) response.get("distance")).intValue());
        assertEquals("2025-08-21T06:00:00Z", response.get("startDate"));

        List<ActivityDbEntity> activityDbEntities = activityRepository.findAllByUserIdOrderByUlidDesc(user.getId());
        assertEquals(1, activityDbEntities.size(), "Expected one activity persisted");
        ActivityDbEntity activityDbEntity = activityDbEntities.getFirst();
        assertEquals(ActivityType.RUNNING, activityDbEntity.getActivityType());
        assertEquals(3600, activityDbEntity.getDuration().toSeconds(), Constant.EPSILON);
        assertEquals(10000, activityDbEntity.getDistance().toMeters(), Constant.EPSILON);
        assertEquals(900, activityDbEntity.getCalories());
        assertEquals("Morning Run", activityDbEntity.getTitle());
        assertEquals("Nice run", activityDbEntity.getDescription());
        assertEquals(Instant.parse("2025-08-21T06:00:00Z"), activityDbEntity.getStartDate());
    }

    @Test
    void postActivity_withoutAuth_returns401() throws Exception {
        mvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "activityType": "walking",
                        "duration": 100,
                        "calories": 10,
                        "title": "Walk",
                        "description": "No token",
                        "distance": 500,
                        "startDate": "2025-08-21T06:00:00Z"
                    }
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void postActivity_withInvalidActivityType_returns400() throws Exception {
        String userUlid = "USER0000000000000000000000";

        String token = accessTokenHelper.getAccessToken(userUlid);
        mvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content("""
                    {
                        "activityType": "NOT_A_TYPE",
                        "duration": 100,
                        "calories": 10,
                        "title": "Title",
                        "description": "Desc",
                        "distance": 1000,
                        "startDate": "2025-08-21T06:00:00Z"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }
}
