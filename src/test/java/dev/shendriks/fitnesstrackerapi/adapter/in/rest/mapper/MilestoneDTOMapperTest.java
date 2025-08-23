package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.MilestoneResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneUlid;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MilestoneDTOMapperTest {
    private final MilestoneDTOMapper mapper = Mappers.getMapper(MilestoneDTOMapper.class);

    private static Milestone buildMilestone(String ulid, boolean completed) {
        return new Milestone(
            new MilestoneId(1L),
            new MilestoneUlid(ulid),
            "10 Runs",
            "Complete 10 running activities",
            "/images/milestones/10-runs.png",
            ActivityType.RUNNING,
            ActivityMetric.ACTIVITY_COUNT,
            10L,
            completed
        );
    }

    @Test
    void toMilestoneResponseDTO_shouldMapAllFields() {
        Milestone milestone = buildMilestone("TESTULID000000000000000001", true);

        MilestoneResponseDTO milestoneResponseDTO = mapper.toMilestoneResponseDTO(milestone);

        assertNotNull(milestoneResponseDTO);
        assertEquals("TESTULID000000000000000001", milestoneResponseDTO.id());
        assertEquals("10 Runs", milestoneResponseDTO.name());
        assertEquals("Complete 10 running activities", milestoneResponseDTO.description());
        assertEquals("/images/milestones/10-runs.png", milestoneResponseDTO.imageFilePath());
        assertTrue(milestoneResponseDTO.isCompleted());
    }

    @Test
    void toMilestoneResponseDTOs_shouldMapList() {
        var milestones = List.of(
            buildMilestone("TESTULID000000000000000002", true),
            buildMilestone("TESTULID000000000000000003", false)
        );

        List<MilestoneResponseDTO> milestoneResponseDTOs = mapper.toMilestoneResponseDTOs(milestones);

        assertEquals(2, milestoneResponseDTOs.size());
        assertEquals("TESTULID000000000000000002", milestoneResponseDTOs.get(0).id());
        assertEquals("TESTULID000000000000000003", milestoneResponseDTOs.get(1).id());
        assertTrue(milestoneResponseDTOs.get(0).isCompleted());
        assertFalse(milestoneResponseDTOs.get(1).isCompleted());
    }

    @Test
    void toMilestoneResponseDTOs_withEmptyList_returnsEmptyList() {
        List<MilestoneResponseDTO> milestoneResponseDTOs = mapper.toMilestoneResponseDTOs(List.of());
        assertNotNull(milestoneResponseDTOs);
        assertTrue(milestoneResponseDTOs.isEmpty());
    }
    
    @Test
    void toMilestoneResponseDTO_withNullInput_returnsNull() {
        assertNull(mapper.toMilestoneResponseDTO(null));
    }

    @Test
    void toMilestoneResponseDTOs_withNullInput_returnsNull() {
        assertNull(mapper.toMilestoneResponseDTOs(null));
    }
}
