package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.MilestoneDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.TrophyDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.MilestoneDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.TrophyDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetMilestonesControllerTest {
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final MilestoneDbEntityRepository milestoneRepository;
    private final TrophyDbEntityRepository trophyRepository;

    public GetMilestonesControllerTest(
        @Autowired MockMvc mvc,
        @Autowired AccessTokenHelper accessTokenHelper,
        @Autowired UserRepository userRepository,
        @Autowired MilestoneDbEntityRepository milestoneRepository,
        @Autowired TrophyDbEntityRepository trophyRepository
    ) {
        this.mvc = mvc;
        this.accessTokenHelper = accessTokenHelper;
        this.userRepository = userRepository;
        this.milestoneRepository = milestoneRepository;
        this.trophyRepository = trophyRepository;
    }

    @Test
    void getMilestones_returnsAllWithCompletionFlagForAuthenticatedUser() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();
        MilestoneDbEntity milestone1 = milestoneRepository.save(MilestoneDbEntity
            .builder()
            .name("5k Distance")
            .description("Desc")
            .imageFilePath("image.png")
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(5_000L)
            .build());
        MilestoneDbEntity milestone2 = milestoneRepository.save(MilestoneDbEntity
            .builder()
            .name("10k Distance")
            .description("Desc")
            .imageFilePath("image.png")
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(10_000L)
            .build());
        trophyRepository.save(TrophyDbEntity
            .builder()
            .user(user)
            .achievement(milestone2)
            .build());

        String token = accessTokenHelper.getAccessToken(userUlid);
        String json = mvc.perform(get("/api/milestones")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> actualMilestones = mapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(2, actualMilestones.size(), "Expected two milestones");
        assertEquals(milestone1.getUlid(), actualMilestones.getFirst().get("id"));
        assertEquals("5k Distance", actualMilestones.getFirst().get("name"));
        assertFalse((Boolean) actualMilestones.getFirst().get("isCompleted"));
        assertNotNull(actualMilestones.get(1).get("id"));
        assertEquals(milestone2.getUlid(), actualMilestones.get(1).get("id"));
        assertEquals("10k Distance", actualMilestones.get(1).get("name"));
        assertTrue((Boolean) actualMilestones.get(1).get("isCompleted"));
    }

    @Test
    void getMilestones_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/milestones").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
