package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.MilestoneDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.TrophyDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ChallengeDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.MilestoneDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.TrophyDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
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
class GetTrophiesControllerTest {
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final MilestoneDbEntityRepository milestoneRepository;
    private final ChallengeDbEntityRepository challengeRepository;
    private final TrophyDbEntityRepository trophyRepository;

    public GetTrophiesControllerTest(
        @Autowired MockMvc mvc,
        @Autowired AccessTokenHelper accessTokenHelper,
        @Autowired UserRepository userRepository,
        @Autowired MilestoneDbEntityRepository milestoneRepository,
        @Autowired ChallengeDbEntityRepository challengeRepository,
        @Autowired TrophyDbEntityRepository trophyRepository
    ) {
        this.mvc = mvc;
        this.accessTokenHelper = accessTokenHelper;
        this.userRepository = userRepository;
        this.milestoneRepository = milestoneRepository;
        this.challengeRepository = challengeRepository;
        this.trophyRepository = trophyRepository;
    }

    @Test
    void getTrophies_returnsUsersTrophies_and401WithoutAuth() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();
        MilestoneDbEntity milestoneDbEntity = milestoneRepository.save(MilestoneDbEntity
            .builder()
            .name("10 Runs")
            .description("Desc")
            .imageFilePath("image.png")
            .activityType(ActivityType.WALKING)
            .activityMetric(ActivityMetric.ACTIVITY_COUNT)
            .completionThreshold(10L)
            .build());
        ChallengeDbEntity challengeDbEntity = challengeRepository.save(ChallengeDbEntity
            .builder()
            .name("August Challenge")
            .description("Desc")
            .imageFilePath("image.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DURATION)
            .completionThreshold(3600L)
            .startDate(Instant.parse("2025-08-01T00:00:00Z"))
            .endDate(Instant.parse("2025-09-01T00:00:00Z"))
            .build());

        trophyRepository.save(TrophyDbEntity.builder().user(user).achievement(milestoneDbEntity).build());
        trophyRepository.save(TrophyDbEntity.builder().user(user).achievement(challengeDbEntity).build());

        String token = accessTokenHelper.getAccessToken(userUlid);
        String json = mvc.perform(get("/api/trophies")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> actualTrophies = mapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(2, actualTrophies.size(), "Expected two trophies");
        assertEquals("challenge", actualTrophies.getFirst().get("achievementType"));
        assertNotNull(actualTrophies.getFirst().get("unlockedAt"));

        @SuppressWarnings("unchecked")
        Map<String, Object> challenge = (Map<String, Object>) actualTrophies.getFirst().get("achievement");
        assertNotNull(challenge);
        assertEquals(challengeDbEntity.getUlid(), challenge.get("id"));

        assertEquals("milestone", actualTrophies.get(1).get("achievementType"));
        assertNotNull(actualTrophies.get(1).get("unlockedAt"));
        @SuppressWarnings("unchecked")
        Map<String, Object> milestone = (Map<String, Object>) actualTrophies.get(1).get("achievement");
        assertNotNull(milestone);
        assertEquals(milestoneDbEntity.getUlid(), milestone.get("id"));
    }

    @Test
    void getTrophies_whenNoAccessToken_returnsError() throws Exception {
        mvc.perform(get("/api/trophies").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
