package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.MilestoneProjection;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneUlid;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MilestoneDbEntityMapperTest {
    private final MilestoneDbEntityMapper mapper = Mappers.getMapper(MilestoneDbEntityMapper.class);

    @Test
    void toMilestone_shouldMapAllFields_andWrapIdTypes() {
        MilestoneProjection projection = MilestoneProjection
            .builder()
            .id(42L)
            .ulid("TESTULID000000000000000001")
            .name("10 Runs")
            .description("Complete 10 running activities")
            .imageFilePath("/images/milestones/10-runs.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.ACTIVITY_COUNT)
            .completionThreshold(10L)
            .isCompleted(true)
            .build();

        Milestone actualMilestone = mapper.toMilestone(projection);

        assertInstanceOf(Milestone.class, actualMilestone);
        assertEquals(new MilestoneId(42L), actualMilestone.getId());
        assertEquals(new MilestoneUlid("TESTULID000000000000000001"), actualMilestone.getUlid());
        assertEquals("10 Runs", actualMilestone.getName());
        assertEquals("Complete 10 running activities", actualMilestone.getDescription());
        assertEquals("/images/milestones/10-runs.png", actualMilestone.getImageFilePath());
        assertEquals(ActivityType.RUNNING, actualMilestone.getActivityType());
        assertEquals(ActivityMetric.ACTIVITY_COUNT, actualMilestone.getActivityMetric());
        assertEquals(10L, actualMilestone.getCompletionThreshold());
        assertTrue(actualMilestone.isCompleted());
    }

    @Test
    void toMilestones_shouldMapList() {
        List<MilestoneProjection> projections = List.of(
            MilestoneProjection
                .builder()
                .id(1L)
                .ulid("TESTULID000000000000000002")
                .name("5 Runs")
                .description("Complete 5 running activities")
                .imageFilePath("/images/milestones/5-runs.png")
                .activityType(ActivityType.RUNNING)
                .activityMetric(ActivityMetric.ACTIVITY_COUNT)
                .completionThreshold(5L)
                .isCompleted(true)
                .build(),
            MilestoneProjection
                .builder()
                .id(2L)
                .ulid("TESTULID000000000000000003")
                .name("100km Cycling")
                .description("Reach 100km total cycling distance")
                .imageFilePath("/images/milestones/100km-cycling.png")
                .activityType(ActivityType.CYCLING)
                .activityMetric(ActivityMetric.TOTAL_DISTANCE)
                .completionThreshold(100_000L)
                .isCompleted(false)
                .build()
        );

        List<Milestone> milestones = mapper.toMilestones(projections);

        assertEquals(2, milestones.size());
        assertEquals(new MilestoneId(1L), milestones.getFirst().getId());
        assertEquals(new MilestoneUlid("TESTULID000000000000000002"), milestones.getFirst().getUlid());
        assertTrue(milestones.getFirst().isCompleted());
        assertEquals(new MilestoneId(2L), milestones.get(1).getId());
        assertEquals(new MilestoneUlid("TESTULID000000000000000003"), milestones.get(1).getUlid());
        assertFalse(milestones.get(1).isCompleted());
    }

    @Test
    void toMilestone_withNullInput_returnsNull() {
        assertNull(mapper.toMilestone(null));
    }

    @Test
    void toMilestones_withNullInput_returnsNull() {
        assertNull(mapper.toMilestones(null));
    }

    @Test
    void toMilestones_withEmptyList_returnsEmptyList() {
        List<Milestone> milestones = mapper.toMilestones(List.of());
        assertNotNull(milestones);
        assertTrue(milestones.isEmpty());
    }
}
