package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ListParticipationsControllerTest {
    public static final Instant NOW = Instant.parse("2025-08-01T00:00:00Z");
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final ChallengeDbEntityRepository challengeRepository;
    private final ChallengeParticipationDbEntityRepository participationRepository;

    public ListParticipationsControllerTest(
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

    private ChallengeDbEntity createAndPersistChallenge(String name, Instant start, Instant end) {
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

    private ChallengeParticipationDbEntity participate(UserDbEntity user, ChallengeDbEntity challenge, int percentage) {
        return participationRepository.save(ChallengeParticipationDbEntity
            .builder()
            .user(user)
            .challenge(challenge)
            .percentageCompleted(percentage)
            .build());
    }

    @Test
    void listParticipations_returnsOnlyCurrentParticipations_and401WithoutAuth() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();
        Instant past1 = NOW.minusSeconds(10);
        Instant past2 = NOW.minusSeconds(5);
        Instant future1 = NOW.plusSeconds(5);
        Instant future2 = NOW.plusSeconds(10);
        ChallengeDbEntity pastChallenge = createAndPersistChallenge("Past Challenge", past1, past2);
        ChallengeDbEntity currentChallenge = createAndPersistChallenge("Current Challenge", past1, future1);
        ChallengeDbEntity futureChallenge = createAndPersistChallenge("Future Challenge", future1, future2);

        participate(user, pastChallenge, 100);
        ChallengeParticipationDbEntity currentParticipation = participate(user, currentChallenge, 25);
        participate(user, futureChallenge, 0);

        String token = accessTokenHelper.getAccessToken(userUlid);
        String json = mvc.perform(get("/api/challenge-participations")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> actualChallengeParticipations = mapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(1, actualChallengeParticipations.size(), "Expected only one challenge participation");
        Map<String, Object> participation = actualChallengeParticipations.getFirst();
        assertEquals(25, ((Number) participation.get("percentageCompleted")).intValue(), "Expected 25% completion");
        assertEquals(currentParticipation.getUlid(), participation.get("id"));
        @SuppressWarnings("unchecked")
        Map<String, Object> challenge = (Map<String, Object>) participation.get("challenge");
        assertEquals("Current Challenge", challenge.get("name"));
        assertEquals(true, challenge.get("hasUserJoined"), "Expected hasUserJoined=true");
        assertEquals(currentChallenge.getUlid(), challenge.get("id"));
    }

    @Test
    void listParticipations_withoutToken_return402() throws Exception {
        mvc.perform(get("/api/challenge-participations").contentType(MediaType.APPLICATION_JSON))
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
