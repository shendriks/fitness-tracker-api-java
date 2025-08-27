package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.challenge;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ChallengeParticipationDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ChallengeDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ChallengeParticipationDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetChallengesControllerTest {
    private static final Instant NOW = Instant.parse("2025-08-01T00:00:00Z");
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final ChallengeDbEntityRepository challengeRepository;
    private final ChallengeParticipationDbEntityRepository participationRepository;

    public GetChallengesControllerTest(
        @Autowired MockMvc mvc,
        @Autowired AccessTokenHelper accessTokenHelper,
        @Autowired UserRepository userRepository,
        @Autowired ChallengeDbEntityRepository challengeRepository,
        @Autowired ChallengeParticipationDbEntityRepository participationRepository
    ) {
        this.mvc = mvc;
        this.accessTokenHelper = accessTokenHelper;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.participationRepository = participationRepository;
    }

    private ChallengeDbEntity createChallenge(String name, Instant start, Instant end) {
        return challengeRepository.save(ChallengeDbEntity
            .builder()
            .name(name)
            .description("Desc")
            .imageFilePath("image.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(10_000L)
            .startDate(start)
            .endDate(end)
            .build());
    }

    @Test
    void getChallenges_returnsOnlyCurrentChallengesWithHasUserJoinedFlag() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();

        var inPast1 = NOW.minusSeconds(10);
        var inPast2 = NOW.minusSeconds(5);
        var inFuture1 = NOW.plusSeconds(5);
        var inFuture2 = NOW.plusSeconds(10);

        createChallenge("Past Challenge", inPast1, inPast2);
        createChallenge("Future Challenge", inFuture1, inFuture2);
        ChallengeDbEntity challengeDbEntity1 = createChallenge("Current not joined Challenge", inPast1, inFuture1);
        ChallengeDbEntity challengeDbEntity2 = createChallenge("Current joined Challenge", inPast1, inFuture1);
        participationRepository.save(ChallengeParticipationDbEntity
            .builder()
            .user(user)
            .challenge(challengeDbEntity2)
            .percentageCompleted(0)
            .build());
        
        String token = accessTokenHelper.getAccessToken(userUlid);
        String json = mvc.perform(get("/api/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> actualChallenges = mapper.readValue(json, new TypeReference<>() {
        });

        assertEquals(2, actualChallenges.size(), "Expected two challenges");
        assertEquals(challengeDbEntity1.getUlid(), actualChallenges.getFirst().get("id"));
        assertFalse((Boolean) actualChallenges.getFirst().get("hasUserJoined"), "Expected hasUserJoined=false");
        assertEquals(challengeDbEntity2.getUlid(), actualChallenges.get(1).get("id"));
        assertTrue((Boolean) actualChallenges.get(1).get("hasUserJoined"), "Expected hasUserJoined=true");
    }

    @Test
    void getChallenges_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/challenges").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    public static class ClockConfig {
        @Bean
        Clock clock() {
            return Clock.fixed(NOW, Clock.systemUTC().getZone());
        }
    }
}
