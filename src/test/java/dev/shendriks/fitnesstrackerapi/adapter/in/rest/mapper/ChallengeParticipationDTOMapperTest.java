package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ChallengeParticipationResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ChallengeResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeParticipationDTOMapperTest {
    private ChallengeParticipationDTOMapper mapper;

    private static Challenge buildChallenge(String ulid) {
        return new Challenge(
            new ChallengeId(123L),
            new ChallengeUlid(ulid),
            "Run 100km in 2025",
            "Put on your running shoes and run 100km in 2025",
            "/images/challenges/run.png",
            ActivityType.RUNNING,
            ActivityMetric.TOTAL_DISTANCE,
            100_000L,
            Instant.parse("2025-01-01T00:00:00Z"),
            Instant.parse("2026-01-01T00:00:00Z"),
            true
        );
    }

    private static ChallengeParticipation buildParticipation(String participationUlid, String challengeUlid, int percentage) {
        return ChallengeParticipation
            .builder()
            .id(new ChallengeParticipationId(1L))
            .ulid(new ChallengeParticipationUlid(participationUlid))
            .userId(new UserId(1L))
            .challenge(buildChallenge(challengeUlid))
            .createdAt(Instant.parse("2025-02-01T12:34:56Z"))
            .updatedAt(Instant.parse("2025-02-02T12:34:56Z"))
            .percentageCompleted(percentage)
            .build();
    }

    @BeforeEach
    void setUp() throws Exception {
        mapper = Mappers.getMapper(ChallengeParticipationDTOMapper.class);
        Field challengeDTOMapperDependency = mapper.getClass().getDeclaredField("challengeDTOMapper");
        challengeDTOMapperDependency.setAccessible(true);
        challengeDTOMapperDependency.set(mapper, Mappers.getMapper(ChallengeDTOMapper.class));
    }

    @Test
    void toChallengeParticipationResponseDTO_shouldMapAllFields() {
        ChallengeParticipation participation = buildParticipation(
            "TESTULID000000000000000001",
            "TESTULID000000000000000002",
            42
        );

        ChallengeParticipationResponseDTO participationDTO = mapper.toChallengeParticipationResponseDTO(participation);

        assertNotNull(participationDTO);
        assertEquals("TESTULID000000000000000001", participationDTO.id());
        assertEquals(Instant.parse("2025-02-01T12:34:56Z"), participationDTO.joinedAt());
        assertEquals(42, participationDTO.percentageCompleted());
        ChallengeResponseDTO challengeDTO = participationDTO.challenge();
        assertNotNull(challengeDTO);
        assertEquals("TESTULID000000000000000002", challengeDTO.id());
        assertEquals("Run 100km in 2025", challengeDTO.name());
        assertEquals("Put on your running shoes and run 100km in 2025", challengeDTO.description());
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), challengeDTO.startDate());
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), challengeDTO.endDate());
        assertEquals("/images/challenges/run.png", challengeDTO.imageFilePath());
        assertTrue(challengeDTO.hasUserJoined());
    }

    @Test
    void toChallengeParticipationResponseDTOs_shouldMapList() {
        List<ChallengeParticipation> participations = List.of(
            buildParticipation("TESTULID000000000000000003", "TESTULID000000000000000004", 10),
            buildParticipation("TESTULID000000000000000005", "TESTULID000000000000000006", 20)
        );

        List<ChallengeParticipationResponseDTO> dtos = mapper.toChallengeParticipationResponseDTOs(participations);

        assertEquals(2, dtos.size());
        assertEquals("TESTULID000000000000000003", dtos.get(0).id());
        assertEquals("TESTULID000000000000000005", dtos.get(1).id());
        assertEquals(10, dtos.get(0).percentageCompleted());
        assertEquals(20, dtos.get(1).percentageCompleted());
        assertEquals("TESTULID000000000000000004", dtos.get(0).challenge().id());
        assertEquals("TESTULID000000000000000006", dtos.get(1).challenge().id());
    }

    @Test
    void toChallengeParticipationResponseDTOs_withEmptyList_returnsEmptyList() {
        List<ChallengeParticipationResponseDTO> dtos = mapper.toChallengeParticipationResponseDTOs(List.of());
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }
}
