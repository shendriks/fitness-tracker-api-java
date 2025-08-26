package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.GPSPositionDbEntity;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetActivityControllerTest {
    private static final double EPSILON = 0.000_000_001;
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final ActivityDbEntityRepository activityRepository;

    public GetActivityControllerTest(
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
    void getActivity_ownActivity_returns200_withDetailsAndGpsPositions() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();

        ActivityDbEntity activity = ActivityDbEntity.builder()
            .user(user)
            .activityType(ActivityType.RUNNING)
            .duration(1800)
            .calories(500)
            .title("Morning Run")
            .description("Nice run")
            .distance(10_000)
            .startDate(Instant.parse("2025-08-21T06:00:00Z"))
            .build();
        GPSPositionDbEntity pos1 = GPSPositionDbEntity.builder()
            .timestamp(Instant.parse("2025-08-21T06:00:10Z"))
            .latitude(0.023)
            .longitude(0.42)
            .activity(activity)
            .build();
        GPSPositionDbEntity pos2 = GPSPositionDbEntity.builder()
            .timestamp(Instant.parse("2025-08-21T06:05:10Z"))
            .latitude(0.0231337)
            .longitude(0.42023)
            .activity(activity)
            .build();
        activity.setGpsPositions(List.of(pos1, pos2));
        activity = activityRepository.save(activity);

        String token = accessTokenHelper.getAccessToken(userUlid);
        String json = mvc.perform(get("/api/activities/" + activity.getUlid())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> detail = mapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(activity.getUlid(), detail.get("id"));
        assertEquals("Morning Run", detail.get("title"));
        assertEquals(1800, detail.get("duration"));
        assertEquals(10000, detail.get("distance"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> gps = (List<Map<String, Object>>) detail.get("gpsPositions");
        assertNotNull(gps);
        assertEquals(2, gps.size(), "Expected two GPS positions");
        assertEquals("2025-08-21T06:00:10Z", gps.getFirst().get("timestamp"));
        assertEquals(0.023, (Double) gps.getFirst().get("latitude"), EPSILON);
        assertEquals(0.42, (Double) gps.getFirst().get("longitude"), EPSILON);
        assertEquals("2025-08-21T06:05:10Z", gps.get(1).get("timestamp"));
        assertEquals(0.0231337, (Double) gps.get(1).get("latitude"), EPSILON);
        assertEquals(0.42023, (Double) gps.get(1).get("longitude"), EPSILON);
    }

    @Test
    void getActivity_nonExisting_returns404() throws Exception {
        String userUlid = "USER0000000000000000000000";
        String nonExistingUlid = "TESTULID000000000000004711";

        String token = accessTokenHelper.getAccessToken(userUlid);
        mvc.perform(get("/api/activities/" + nonExistingUlid)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }

    @Test
    void getActivity_ofAnotherUser_returns404() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity otherUser = userRepository.saveAndFlush(UserDbEntity.builder()
            .name("Bob")
            .email("bob@example.com")
            .password("password")
            .authority("ROLE_USER")
            .accountType(AccountType.BASIC)
            .build());
        ActivityDbEntity othersActivity = activityRepository.save(ActivityDbEntity.builder()
            .user(otherUser)
            .activityType(ActivityType.CYCLING)
            .duration(3600)
            .calories(800)
            .title("Other User Ride")
            .description("Not yours")
            .distance(25000)
            .startDate(Instant.parse("2025-08-22T18:30:00Z"))
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);
        mvc.perform(get("/api/activities/" + othersActivity.getUlid())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }
}
