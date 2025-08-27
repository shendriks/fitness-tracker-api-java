package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.AchievementResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.TrophyResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.enums.AchievementType;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrophyDTOMapperTest {
    private final TrophyDTOMapper mapper = Mappers.getMapper(TrophyDTOMapper.class);

    private static Milestone buildMilestone(String ulid) {
        return new Milestone(
            new MilestoneId(100L),
            new MilestoneUlid(ulid),
            "10 Runs",
            "Complete 10 running activities",
            "/images/milestones/10-runs.png",
            ActivityType.RUNNING,
            ActivityMetric.ACTIVITY_COUNT,
            10L,
            true
        );
    }

    private static Trophy buildTrophy(String ulid, String achievementUlid, AchievementType type, Instant unlockedAt) {
        Milestone milestone = buildMilestone(achievementUlid);
        return Trophy
            .builder()
            .id(new TrophyId(1L))
            .ulid(new TrophyUlid(ulid))
            .userId(new UserId(999L))
            .achievement(milestone)
            .achievementType(type)
            .unlockedAt(unlockedAt)
            .build();
    }

    @Test
    void toTrophyResponseDTO_mapsAllFields() {
        Instant unlockedAt = Instant.parse("2025-08-22T12:00:00Z");
        Trophy trophy = buildTrophy(
            "TESTULID000000000000000001",
            "TESTULID000000000000000002",
            AchievementType.MILESTONE,
            unlockedAt
        );

        TrophyResponseDTO trophyResponseDTO = mapper.toTrophyResponseDTO(trophy);

        assertNotNull(trophyResponseDTO);
        assertEquals("TESTULID000000000000000001", trophyResponseDTO.id());
        assertEquals(AchievementType.MILESTONE, trophyResponseDTO.achievementType());
        assertEquals(unlockedAt, trophyResponseDTO.unlockedAt());
        AchievementResponseDTO achievementResponseDTO = trophyResponseDTO.achievement();
        assertNotNull(achievementResponseDTO);
        assertEquals("TESTULID000000000000000002", achievementResponseDTO.id());
        assertEquals("10 Runs", achievementResponseDTO.name());
        assertEquals("Complete 10 running activities", achievementResponseDTO.description());
        assertEquals("/images/milestones/10-runs.png", achievementResponseDTO.imageFilePath());
    }

    @Test
    void toTrophyResponseDTOs_mapsList() {
        var trophies = List.of(
            buildTrophy(
                "TESTULID000000000000000003",
                "TESTULID000000000000000004",
                AchievementType.MILESTONE,
                Instant.parse("2025-08-20T00:00:00Z")
            ),
            buildTrophy(
                "TESTULID000000000000000005",
                "TESTULID000000000000000006",
                AchievementType.CHALLENGE,
                Instant.parse("2025-08-21T00:00:00Z")
            )
        );

        List<TrophyResponseDTO> trophyResponseDTOs = mapper.toTrophyResponseDTOs(trophies);

        assertEquals(2, trophyResponseDTOs.size());
        assertEquals("TESTULID000000000000000003", trophyResponseDTOs.get(0).id());
        assertEquals("TESTULID000000000000000005", trophyResponseDTOs.get(1).id());
        assertEquals(AchievementType.MILESTONE, trophyResponseDTOs.get(0).achievementType());
        assertEquals(AchievementType.CHALLENGE, trophyResponseDTOs.get(1).achievementType());
        assertEquals("TESTULID000000000000000004", trophyResponseDTOs.get(0).achievement().id());
        assertEquals("TESTULID000000000000000006", trophyResponseDTOs.get(1).achievement().id());
    }

    @Test
    void toTrophyResponseDTOs_withEmptyList_returnsEmptyList() {
        List<TrophyResponseDTO> trophyResponseDTOs = mapper.toTrophyResponseDTOs(List.of());
        assertNotNull(trophyResponseDTOs);
        assertTrue(trophyResponseDTOs.isEmpty());
    }

    @Test
    void toTrophyResponseDTO_withNullInput_returnsNull() {
        assertNull(mapper.toTrophyResponseDTO(null));
    }

    @Test
    void toTrophyResponseDTOs_withNullInput_returnsNull() {
        assertNull(mapper.toTrophyResponseDTOs(null));
    }
}
