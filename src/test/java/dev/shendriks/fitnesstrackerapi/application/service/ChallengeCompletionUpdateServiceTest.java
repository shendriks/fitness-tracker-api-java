package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.ChallengeBecameIncompleteEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ChallengeCompletedEvent;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallengeParticipations;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAggregatingActivities;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.service.AchievementCompletionCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

class ChallengeCompletionUpdateServiceTest {
    private final UserId userId = new UserId(7L);
    private final ChallengeParticipationId participationId = new ChallengeParticipationId(55L);
    private final ChallengeId challengeId = new ChallengeId(101L);
    private ApplicationEventPublisher eventPublisher;
    private ForAccessingChallengeParticipations forAccessingChallengeParticipations;
    private ForAggregatingActivities forAggregatingActivities;
    private AchievementCompletionCalculator achievementCompletionCalculator;
    private TrophyManagementService trophyManagementService;
    private ChallengeCompletionUpdateService service;

    private ChallengeParticipation buildParticipationWithCurrentPercentage(int currentPercentage) {
        return ChallengeParticipation
            .builder()
            .id(participationId)
            .ulid(new ChallengeParticipationUlid("TESTULID000000000000000001"))
            .userId(userId)
            .challenge(Challenge
                .builder()
                .id(challengeId)
                .ulid(new ChallengeUlid("TESTULID000000000000000002"))
                .name("Name")
                .description("Desc")
                .imageFilePath("img.png")
                .activityType(ActivityType.RUNNING)
                .activityMetric(ActivityMetric.TOTAL_DISTANCE)
                .completionThreshold(10_000L)
                .startDate(Instant.now())
                .endDate(Instant.now())
                .hasUserJoined(true)
                .build())
            .createdAt(Instant.now().minusSeconds(100_000))
            .updatedAt(Instant.now())
            .percentageCompleted(currentPercentage)
            .build();
    }

    @BeforeEach
    void setUp() {
        eventPublisher = mock(ApplicationEventPublisher.class);
        forAccessingChallengeParticipations = mock(ForAccessingChallengeParticipations.class);
        forAggregatingActivities = mock(ForAggregatingActivities.class);
        achievementCompletionCalculator = mock(AchievementCompletionCalculator.class);
        trophyManagementService = mock(TrophyManagementService.class);

        service = new ChallengeCompletionUpdateService(
            eventPublisher,
            forAccessingChallengeParticipations,
            forAggregatingActivities,
            achievementCompletionCalculator,
            trophyManagementService
        );

        // Aggregation data is irrelevant because the calculator is mocked
        when(forAggregatingActivities.aggregateForUserByTypeInTimeRange(any(), any(), any()))
            .thenReturn(ActivityAggregationMap.create(
                ActivityAggregation.zero(),
                List.of()
            ));
    }

    @Test
    void updateChallengeCompletion_whenBecameComplete_publishesEvent_andCreatesTrophy_andUpdatesPercentage() {
        ChallengeParticipation participation = buildParticipationWithCurrentPercentage(0);
        when(achievementCompletionCalculator.calculateAchievementCompletion(any()))
            .thenReturn(AchievementCompletionResult
                .builder()
                .percentageCompleted(100)
                .becameComplete(true)
                .becameIncomplete(false)
                .build());

        service.updateChallengeCompletion(participation);

        verify(forAccessingChallengeParticipations).updatePercentageCompleted(participationId, 100);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(ChallengeCompletedEvent.class, event);
        ChallengeCompletedEvent challengeCompletedEvent = (ChallengeCompletedEvent) event;
        assertEquals(userId, challengeCompletedEvent.userId());
        assertEquals(challengeId, challengeCompletedEvent.challengeId());

        verify(trophyManagementService).createTrophyIfNotExists(userId, challengeId);
        verify(trophyManagementService, never()).deleteTrophyIfExists(any(), any(ChallengeId.class));
    }

    @Test
    void updateChallengeCompletion_whenBecameIncomplete_publishesEvent_andDeletesTrophy_andUpdatesPercentage() {
        ChallengeParticipation participation = buildParticipationWithCurrentPercentage(100);
        when(achievementCompletionCalculator.calculateAchievementCompletion(any()))
            .thenReturn(AchievementCompletionResult
                .builder()
                .percentageCompleted(80)
                .becameComplete(false)
                .becameIncomplete(true)
                .build()
            );

        service.updateChallengeCompletion(participation);

        verify(forAccessingChallengeParticipations).updatePercentageCompleted(participationId, 80);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(ChallengeBecameIncompleteEvent.class, event);
        ChallengeBecameIncompleteEvent challengeBecameIncompleteEvent = (ChallengeBecameIncompleteEvent) event;
        assertEquals(userId, challengeBecameIncompleteEvent.userId());
        assertEquals(challengeId, challengeBecameIncompleteEvent.challengeId());

        verify(trophyManagementService).deleteTrophyIfExists(userId, challengeId);
        verify(trophyManagementService, never()).createTrophyIfNotExists(any(), any());
    }

    @Test
    void updateChallengeCompletion_whenNoTransition_onlyUpdatesPercentage_andNoEventsOrTrophies() {
        ChallengeParticipation participation = buildParticipationWithCurrentPercentage(50);
        when(achievementCompletionCalculator.calculateAchievementCompletion(any()))
            .thenReturn(AchievementCompletionResult
                .builder()
                .percentageCompleted(60)
                .becameComplete(false)
                .becameIncomplete(false)
                .build()
            );

        service.updateChallengeCompletion(participation);

        verify(forAccessingChallengeParticipations).updatePercentageCompleted(participationId, 60);
        verifyNoInteractions(eventPublisher);
        verifyNoInteractions(trophyManagementService);
    }

    @Test
    void updateAllChallengeCompletionsForUserForUser_delegatesForEachParticipation() {
        when(forAccessingChallengeParticipations.findCurrentByUser(userId)).thenReturn(List.of(
            buildParticipationWithCurrentPercentage(10),
            buildParticipationWithCurrentPercentage(20)
        ));

        when(achievementCompletionCalculator.calculateAchievementCompletion(any()))
            .thenReturn(AchievementCompletionResult
                .builder()
                .percentageCompleted(23)
                .becameComplete(false)
                .becameIncomplete(false)
                .build()
            );

        service.updateAllChallengeCompletionsForUser(userId);

        verify(forAccessingChallengeParticipations).findCurrentByUser(userId);
        verify(forAccessingChallengeParticipations, times(2))
            .updatePercentageCompleted(any(ChallengeParticipationId.class), eq(23));

        verify(forAggregatingActivities, times(2))
            .aggregateForUserByTypeInTimeRange(eq(userId), any(), any());

        verify(achievementCompletionCalculator, times(2)).calculateAchievementCompletion(any());
    }
}
