package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.challenge;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LeaveChallengeControllerTest {
    private static final Instant NOW = Instant.parse("2025-08-01T00:00:00Z");
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final ChallengeDbEntityRepository challengeRepository;
    private final ChallengeParticipationDbEntityRepository participationRepository;

    public LeaveChallengeControllerTest(
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
    void leaveChallenge_withExistingParticipation_deletesAndReturns204AndIsIdempotent() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();
        Instant past = NOW.minusSeconds(10);
        Instant future = NOW.plusSeconds(10);
        ChallengeDbEntity challenge = createChallenge("August Challenge", past, future);
        participationRepository.save(ChallengeParticipationDbEntity
            .builder()
            .user(user)
            .challenge(challenge)
            .percentageCompleted(0)
            .build());
        
        Optional<ChallengeParticipationDbEntity> afterJoin = participationRepository.findByUserIdAndChallengeUlid(user.getId(), challenge.getUlid());
        assertTrue(afterJoin.isPresent(), "Expected participation to be present after joining");

        String token = accessTokenHelper.getAccessToken(userUlid);

        // First leave
        mvc.perform(post("/api/challenges/" + challenge.getUlid() + "/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());

        Optional<ChallengeParticipationDbEntity> afterLeave = participationRepository.findByUserIdAndChallengeUlid(user.getId(), challenge.getUlid());
        assertTrue(afterLeave.isEmpty(), "Expected participation to be deleted after leaving");

        // Second leave should be idempotent
        mvc.perform(post("/api/challenges/" + challenge.getUlid() + "/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());

        List<ChallengeParticipationDbEntity> actualParticipations = participationRepository.findCurrent(user.getId(), NOW);
        assertEquals(0L, actualParticipations.size(), "Expected no participation after leaving");
    }

    @Test
    void leaveChallenge_withNonExistingChallenge_returns404() throws Exception {
        String userUlid = "USER0000000000000000000000";
        String token = accessTokenHelper.getAccessToken(userUlid);
        mvc.perform(post("/api/challenges/TESTULID000000000000000001/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }

    @Test
    void leaveChallenge_withoutAuth_returns401() throws Exception {
        ChallengeDbEntity challenge = createChallenge("Auth Test", NOW, NOW);
        mvc.perform(post("/api/challenges/" + challenge.getUlid() + "/leave")
                .contentType(MediaType.APPLICATION_JSON))
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
