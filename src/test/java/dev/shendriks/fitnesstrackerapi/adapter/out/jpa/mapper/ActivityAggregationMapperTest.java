package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityAggregationDbProjection;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityTypeAggregationDbProjection;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import dev.shendriks.fitnesstrackerapi.infrastructure.Constant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActivityAggregationMapperTest {
    private final ActivityAggregationMapper mapper = Mappers.getMapper(ActivityAggregationMapper.class);

    @Test
    void toActivityAggregation_mapsAllFields() {
        ActivityAggregationDbProjection projection = new ActivityAggregationDbProjection(
            5L,
            12345.0,
            6789.0,
//            new BigDecimal("12345.0"), 
//            new BigDecimal("6789.0"),
            4000.0,
            3600.0
//            new BigDecimal("4000.0"),
//            new BigDecimal("3600.0")
        );
//            .builder()
//            .count(5L)
//            .totalDistance(Distance.ofMeters(12345.0))
//            .totalDuration(Duration.ofSeconds(6789.0))
//            .maxDistance(Distance.ofMeters(4000.0))
//            .maxDuration(Duration.ofSeconds(3600.0))
//            .build();

        ActivityAggregation actualAggregation = mapper.toActivityAggregation(projection);

        assertNotNull(actualAggregation);
        assertEquals(5L, actualAggregation.count());
        assertEquals(12345.0, actualAggregation.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(6789.0, actualAggregation.totalDuration().toSeconds(), Constant.EPSILON);
        assertEquals(4000.0, actualAggregation.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(3600.0, actualAggregation.maxDuration().toSeconds(), Constant.EPSILON);
    }

    @Test
    void toActivityTypeAggregations_mapsList() {
        List<ActivityTypeAggregationDbProjection> projections = List.of(
            new ActivityTypeAggregationDbProjection(
                ActivityType.RUNNING.getValue(),
                3L,
                9000.0,
                3600.0,
                5000.0,
                1800.00
            ),
//                .builder()
//                .type(ActivityType.RUNNING)
//                .count(3L)
//                .totalDistance(Distance.ofMeters(9000.0))
//                .totalDuration(Duration.ofSeconds(3600.0))
//                .maxDistance(Distance.ofMeters(5000.0))
//                .maxDuration(Duration.ofSeconds(1800.0))
//                .build(),
            new ActivityTypeAggregationDbProjection(
                ActivityType.CYCLING.getValue(),
                2L,
                20000.0,
                4000.0,
                15000.0,
                2500.0
            )
//                .builder()
//                .type(ActivityType.CYCLING)
//                .count(2L)
//                .totalDistance(Distance.ofMeters(20000.0))
//                .totalDuration(Duration.ofSeconds(4000.0))
//                .maxDistance(Distance.ofMeters(15000.0))
//                .maxDuration(Duration.ofSeconds(2500.0))
//                .build()
        );

        List<ActivityTypeAggregation> actualTypeAggregation = mapper.toActivityTypeAggregations(projections);

        assertEquals(2, actualTypeAggregation.size());
        ActivityTypeAggregation runningAggregation = actualTypeAggregation.get(0);
        ActivityTypeAggregation cyclingAggregation = actualTypeAggregation.get(1);
        assertEquals(ActivityType.RUNNING, runningAggregation.type());
        assertEquals(3L, runningAggregation.count());
        assertEquals(9000.0, runningAggregation.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(3600.0, runningAggregation.totalDuration().toSeconds(), Constant.EPSILON);
        assertEquals(5000.0, runningAggregation.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(1800.0, runningAggregation.maxDuration().toSeconds(), Constant.EPSILON);
        assertEquals(ActivityType.CYCLING, cyclingAggregation.type());
        assertEquals(2L, cyclingAggregation.count());
        assertEquals(20000.0, cyclingAggregation.totalDistance().toMeters(), Constant.EPSILON);
        assertEquals(4000.0, cyclingAggregation.totalDuration().toSeconds(), Constant.EPSILON);
        assertEquals(15000.0, cyclingAggregation.maxDistance().toMeters(), Constant.EPSILON);
        assertEquals(2500.0, cyclingAggregation.maxDuration().toSeconds(), Constant.EPSILON);
    }

    @Test
    void toActivityAggregationMap_combinesAndPadsMissingTypes() {
        ActivityAggregationDbProjection total = new ActivityAggregationDbProjection(
            10L,
            50000.0,
            10000.0,
//            new BigDecimal("50000.0"),
//            new BigDecimal("10000.0"),
            20000.0,
            4000.0
//            new BigDecimal("20000.0"),
//            new BigDecimal("4000.0")
        );
//            .builder()
//            .count(10L)
//            .totalDistance(Distance.ofMeters(50000L))
//            .totalDuration(Duration.ofSeconds(10000L))
//            .maxDistance(Distance.ofMeters(20000L))
//            .maxDuration(Duration.ofSeconds(4000L))
//            .build();
        List<ActivityTypeAggregationDbProjection> byTypeProjections = List.of(
            new ActivityTypeAggregationDbProjection(
                ActivityType.RUNNING.getValue(), 6L, 18000.0, 5000.0, 7000.0, 2000.0
            ),
//                .builder()
//                .type(ActivityType.RUNNING)
//                .count(6L)
//                .totalDistance(Distance.ofMeters(18000L))
//                .totalDuration(Duration.ofSeconds(5000L))
//                .maxDistance(Distance.ofMeters(7000L))
//                .maxDuration(Duration.ofSeconds(2000L))
//                .build(),
            new ActivityTypeAggregationDbProjection(
                ActivityType.CYCLING.getValue(),
                4L,
                32000.0,
                5000.0,
                20000.0,
                2500.0
            )
//                .builder()
//                .type(ActivityType.CYCLING)
//                .count(4L)
//                .totalDistance(Distance.ofMeters(32000L))
//                .totalDuration(Duration.ofSeconds(5000L))
//                .maxDistance(Distance.ofMeters(20000L))
//                .maxDuration(Duration.ofSeconds(2500L))
//                .build()
        );

        ActivityAggregationMap actualActivityAggregationMap = mapper.toActivityAggregationMap(total, byTypeProjections);

        assertEquals(
            ActivityAggregation
                .builder()
                .count(10L)
                .totalDistance(Distance.ofMeters(50000L))
                .totalDuration(Duration.ofSeconds(10000L))
                .maxDistance(Distance.ofMeters(20000L))
                .maxDuration(Duration.ofSeconds(4000L))
                .build(),
            actualActivityAggregationMap.getTotal()
        );
        assertEquals(
            ActivityAggregation
                .builder()
                .count(6L)
                .totalDistance(Distance.ofMeters(18000L))
                .totalDuration(Duration.ofSeconds(5000L))
                .maxDistance(Distance.ofMeters(7000L))
                .maxDuration(Duration.ofSeconds(2000L))
                .build(),
            actualActivityAggregationMap.getByType(ActivityType.RUNNING)
        );
        assertEquals(
            ActivityAggregation
                .builder()
                .count(4L)
                .totalDistance(Distance.ofMeters(32000L))
                .totalDuration(Duration.ofSeconds(5000L))
                .maxDistance(Distance.ofMeters(20000L))
                .maxDuration(Duration.ofSeconds(2500L))
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
