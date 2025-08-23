package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityAggregationDbProjection;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityTypeAggregationDbProjection;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregation;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityTypeAggregation;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActivityAggregationMapperTest {
    private final ActivityAggregationMapper mapper = Mappers.getMapper(ActivityAggregationMapper.class);

    @Test
    void toActivityAggregation_shouldMapAllFields() {
        ActivityAggregationDbProjection projection = ActivityAggregationDbProjection
            .builder()
            .count(5L)
            .totalDistance(12345L)
            .totalDuration(6789L)
            .maxDistance(4000L)
            .maxDuration(3600L)
            .build();

        ActivityAggregation actualAggregation = mapper.toActivityAggregation(projection);

        assertNotNull(actualAggregation);
        assertEquals(5L, actualAggregation.count());
        assertEquals(12345L, actualAggregation.totalDistance());
        assertEquals(6789L, actualAggregation.totalDuration());
        assertEquals(4000L, actualAggregation.maxDistance());
        assertEquals(3600L, actualAggregation.maxDuration());
    }

    @Test
    void toActivityTypeAggregations_shouldMapList() {
        List<ActivityTypeAggregationDbProjection> projections = List.of(
            ActivityTypeAggregationDbProjection
                .builder()
                .type(ActivityType.RUNNING)
                .count(3L)
                .totalDistance(9000L)
                .totalDuration(3600L)
                .maxDistance(5000L)
                .maxDuration(1800L)
                .build(),
            ActivityTypeAggregationDbProjection
                .builder()
                .type(ActivityType.CYCLING)
                .count(2L)
                .totalDistance(20000L)
                .totalDuration(4000L)
                .maxDistance(15000L)
                .maxDuration(2500L)
                .build()
        );

        List<ActivityTypeAggregation> actualTypeAggregation = mapper.toActivityTypeAggregations(projections);

        assertEquals(2, actualTypeAggregation.size());
        ActivityTypeAggregation runningAggregation = actualTypeAggregation.get(0);
        ActivityTypeAggregation cyclingAggregation = actualTypeAggregation.get(1);
        assertEquals(ActivityType.RUNNING, runningAggregation.type());
        assertEquals(3L, runningAggregation.count());
        assertEquals(9000L, runningAggregation.totalDistance());
        assertEquals(3600L, runningAggregation.totalDuration());
        assertEquals(5000L, runningAggregation.maxDistance());
        assertEquals(1800L, runningAggregation.maxDuration());
        assertEquals(ActivityType.CYCLING, cyclingAggregation.type());
        assertEquals(2L, cyclingAggregation.count());
        assertEquals(20000L, cyclingAggregation.totalDistance());
        assertEquals(4000L, cyclingAggregation.totalDuration());
        assertEquals(15000L, cyclingAggregation.maxDistance());
        assertEquals(2500L, cyclingAggregation.maxDuration());
    }

    @Test
    void toActivityAggregationMap_shouldCombineAndPadMissingTypes() {
        ActivityAggregationDbProjection total = ActivityAggregationDbProjection
            .builder()
            .count(10L)
            .totalDistance(50000L)
            .totalDuration(10000L)
            .maxDistance(20000L)
            .maxDuration(4000L)
            .build();
        List<ActivityTypeAggregationDbProjection> byTypeProjections = List.of(
            ActivityTypeAggregationDbProjection
                .builder()
                .type(ActivityType.RUNNING)
                .count(6L)
                .totalDistance(18000L)
                .totalDuration(5000L)
                .maxDistance(7000L)
                .maxDuration(2000L)
                .build(),
            ActivityTypeAggregationDbProjection
                .builder()
                .type(ActivityType.CYCLING)
                .count(4L)
                .totalDistance(32000L)
                .totalDuration(5000L)
                .maxDistance(20000L)
                .maxDuration(2500L)
                .build()
        );

        ActivityAggregationMap actualActivityAggregationMap = mapper.toActivityAggregationMap(total, byTypeProjections);

        assertEquals(
            ActivityAggregation
                .builder()
                .count(10L)
                .totalDistance(50000L)
                .totalDuration(10000L)
                .maxDistance(20000L)
                .maxDuration(4000L)
                .build(),
            actualActivityAggregationMap.getTotal()
        );
        assertEquals(
            ActivityAggregation
                .builder()
                .count(6L)
                .totalDistance(18000L)
                .totalDuration(5000L)
                .maxDistance(7000L)
                .maxDuration(2000L)
                .build(),
            actualActivityAggregationMap.getByType(ActivityType.RUNNING)
        );
        assertEquals(
            ActivityAggregation
                .builder()
                .count(4L)
                .totalDistance(32000L)
                .totalDuration(5000L)
                .maxDistance(20000L)
                .maxDuration(2500L)
                .build(),
            actualActivityAggregationMap.getByType(ActivityType.CYCLING)
        );
        assertEquals(ActivityAggregation.zero(), actualActivityAggregationMap.getByType(ActivityType.WALKING));
        assertEquals(ActivityAggregation.zero(), actualActivityAggregationMap.getByType(ActivityType.SWIMMING));
        assertEquals(ActivityAggregation.zero(), actualActivityAggregationMap.getByType(ActivityType.MOUNTAIN_BIKING));
    }
    
    @Test
    void toActivityAggregation_withNullInput_returnsNull() {
        assertNull(mapper.toActivityAggregation(null));
    }

    @Test
    void toActivityTypeAggregations_withNullInput_returnsNull() {
        assertNull(mapper.toActivityTypeAggregations(null));
    }
}
