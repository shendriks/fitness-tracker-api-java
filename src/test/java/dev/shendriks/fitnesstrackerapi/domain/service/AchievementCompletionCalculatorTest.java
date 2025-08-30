package dev.shendriks.fitnesstrackerapi.domain.service;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AchievementCompletionCalculatorTest {
    private AchievementCompletionCalculator calculator;

    private static ActivityAggregationMap buildAggregationMap() {
        ActivityAggregation total = ActivityAggregation
            .builder()
            .count(15)
            .totalDistance(Distance.ofMeters(15000.0))
            .totalDuration(Duration.ofSeconds(9000L))
            .maxDistance(Distance.ofMeters(10000.0))
            .maxDuration(Duration.ofSeconds(3600L))
            .build();

        List<ActivityTypeAggregation> byType = List.of(
            ActivityTypeAggregation
                .builder()
                .type(ActivityType.RUNNING)
                .count(7)
                .totalDistance(Distance.ofMeters(7000.0))
                .totalDuration(Duration.ofSeconds(4200L))
                .maxDistance(Distance.ofMeters(5000.0))
                .maxDuration(Duration.ofSeconds(2000L))
                .build(),
            ActivityTypeAggregation
                .builder()
                .type(ActivityType.CYCLING)
                .count(8)
                .totalDistance(Distance.ofMeters(8000.0))
                .totalDuration(Duration.ofSeconds(4800L))
                .maxDistance(Distance.ofMeters(10000.0))
                .maxDuration(Duration.ofSeconds(3600L))
                .build()
        );

        return ActivityAggregationMap.create(total, byType);
    }

    public static Stream<Arguments> provideMetricsForMetricMappingTest() {
        return Stream.of(
            Arguments.of(ActivityMetric.ACTIVITY_COUNT, 30L, 50),
            Arguments.of(ActivityMetric.LONGEST_SINGLE_DURATION, 7200L, 50),
            Arguments.of(ActivityMetric.LONGEST_SINGLE_DISTANCE, 20000L, 50),
            Arguments.of(ActivityMetric.TOTAL_DURATION, 10000L, 90),
            Arguments.of(ActivityMetric.TOTAL_DISTANCE, 30000L, 50)
        );
    }

    public static Stream<Arguments> provideMetricsForTransitionTest() {
        return Stream.of(
            Arguments.of(null, ActivityMetric.TOTAL_DISTANCE, 80, 15000L, true, false),
            Arguments.of(null, ActivityMetric.LONGEST_SINGLE_DISTANCE, 100, 10000L, false, false),
            Arguments.of(ActivityType.RUNNING, ActivityMetric.TOTAL_DURATION, 100, 10000L, false, true),
            Arguments.of(ActivityType.RUNNING, ActivityMetric.ACTIVITY_COUNT, 20, 20L, false, false)
        );
    }

    @BeforeEach
    void setUp() {
        calculator = new AchievementCompletionCalculator();
    }

    @Test
    void calculateAchievementCompletion_withTypeIsNull_usesTotalAggregation() {
        ActivityAggregationMap map = buildAggregationMap();
        AchievementCompletionRequest request = AchievementCompletionRequest
            .builder()
            .activityAggregationMap(map)
            .activityMetric(ActivityMetric.ACTIVITY_COUNT)
            .completionThreshold(15L)
            .build();

        AchievementCompletionResult result = calculator.calculateAchievementCompletion(request);

        assertEquals(100, result.percentageCompleted(), "Expected 100% completion");
        assertTrue(result.becameComplete(), "Expected to become complete");
        assertFalse(result.becameIncomplete(), "Expected not to become incomplete");
    }

    @Test
    void calculateAchievementCompletion_withTypeSpecified_usesByTypeAggregation() {
        ActivityAggregationMap map = buildAggregationMap();
        AchievementCompletionRequest request = AchievementCompletionRequest
            .builder()
            .activityAggregationMap(map)
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(10000L)
            .build();

        AchievementCompletionResult result = calculator.calculateAchievementCompletion(request);

        assertEquals(70, result.percentageCompleted(), "Expected 70% completion");
        assertFalse(result.becameComplete(), "Expected not to become complete");
        assertFalse(result.becameIncomplete(), "Expected not to become incomplete");
    }

    @ParameterizedTest
    @MethodSource("provideMetricsForMetricMappingTest")
    void calculateAchievementCompletion_mapsMetricsCorrectly(ActivityMetric metric, Long completionThreshold, int expectedPercentageCompleted) {
        ActivityAggregationMap map = buildAggregationMap();
        AchievementCompletionRequest request = AchievementCompletionRequest
            .builder()
            .activityAggregationMap(map)
            .activityMetric(metric)
            .completionThreshold(completionThreshold)
            .build();

        AchievementCompletionResult result = calculator.calculateAchievementCompletion(request);

        assertEquals(
            expectedPercentageCompleted,
            result.percentageCompleted(),
            "Expected %d%% completion".formatted(expectedPercentageCompleted)
        );
    }

    @Test
    @DisplayName("Percentage is capped at 100")
    void calculateAchievementCompletion_capsPercentageAt100() {
        ActivityAggregationMap map = buildAggregationMap();
        AchievementCompletionRequest request = AchievementCompletionRequest.builder()
            .activityAggregationMap(map)
            .activityMetric(ActivityMetric.ACTIVITY_COUNT)
            .completionThreshold(14L)
            .build();

        AchievementCompletionResult result = calculator.calculateAchievementCompletion(request);

        assertEquals(100, result.percentageCompleted(), "Expected 100% completion");
    }

    @Test
    @DisplayName("Uses integer division semantics")
    void calculateAchievementCompletion_usesIntegerDivision() {
        ActivityAggregationMap map = buildAggregationMap();
        AchievementCompletionRequest request = AchievementCompletionRequest
            .builder()
            .activityAggregationMap(map)
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(20000L)
            .build();

        AchievementCompletionResult result = calculator.calculateAchievementCompletion(request);

        assertEquals(35, result.percentageCompleted(), "Expected 35% completion");
    }

    @ParameterizedTest
    @MethodSource("provideMetricsForTransitionTest")
    @DisplayName("Became complete/incomplete flags reflect transitions correctly")
    void calculateAchievementCompletion_setsTransitionFlagsCorrectly(
        ActivityType activityType,
        ActivityMetric metric,
        int currentPercentageCompleted,
        Long completionThreshold,
        boolean expectedBecameComplete,
        boolean expectedBecameIncomplete
    ) {
        ActivityAggregationMap map = buildAggregationMap();
        AchievementCompletionRequest request = AchievementCompletionRequest
            .builder()
            .activityAggregationMap(map)
            .activityType(activityType)
            .activityMetric(metric)
            .currentPercentageCompleted(currentPercentageCompleted)
            .completionThreshold(completionThreshold)
            .build();

        AchievementCompletionResult result = calculator.calculateAchievementCompletion(request);

        assertEquals(
            expectedBecameComplete,
            result.becameComplete(),
            "Expected becameComplete to be %s".formatted(expectedBecameComplete)
        );
        assertEquals(
            expectedBecameIncomplete,
            result.becameIncomplete(),
            "Expected becameIncomplete to be %s".formatted(expectedBecameComplete)
        );
    }
}
