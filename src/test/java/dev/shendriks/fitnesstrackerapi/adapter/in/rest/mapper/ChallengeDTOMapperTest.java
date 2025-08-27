package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ChallengeResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeDTOMapperTest {
    private final ChallengeDTOMapper mapper = Mappers.getMapper(ChallengeDTOMapper.class);

    private static Challenge buildChallenge(String ulid, boolean hasUserJoined) {
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
            hasUserJoined
        );
    }

    @Test
    void toChallengeResponse_mapsAllFieldsAndUlidToId() {
        Challenge challenge = buildChallenge("TESTULID000000000000000000", true);

        ChallengeResponseDTO dto = mapper.toChallengeResponse(challenge);

        assertNotNull(dto);
        assertEquals("TESTULID000000000000000000", dto.id());
        assertEquals("Run 100km in 2025", dto.name());
        assertEquals("Put on your running shoes and run 100km in 2025", dto.description());
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), dto.startDate());
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), dto.endDate());
        assertEquals("/images/challenges/run.png", dto.imageFilePath());
        assertTrue(dto.hasUserJoined());
    }

    @Test
    void toChallengeResponses_mapsList() {
        var challenges = List.of(
            buildChallenge("TESTULID000000000000000001", true),
            buildChallenge("TESTULID000000000000000002", false)
        );

        List<ChallengeResponseDTO> dtos = mapper.toChallengeResponses(challenges);

        assertEquals(2, dtos.size());
        assertEquals("TESTULID000000000000000001", dtos.get(0).id());
        assertEquals("TESTULID000000000000000002", dtos.get(1).id());
        assertTrue(dtos.get(0).hasUserJoined());
        assertFalse(dtos.get(1).hasUserJoined());
    }

    @Test
    void toChallengeResponse_withNullInput_returnsNull() {
        assertNull(mapper.toChallengeResponse(null));
    }

    @Test
    void toChallengeResponses_withNullInput_returnsNull() {
        assertNull(mapper.toChallengeResponses(null));
    }
}
