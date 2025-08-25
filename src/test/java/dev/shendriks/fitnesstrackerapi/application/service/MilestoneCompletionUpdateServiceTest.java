package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.MilestoneBecameIncompleteEvent;
import dev.shendriks.fitnesstrackerapi.application.event.MilestoneCompletedEvent;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingMilestones;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAggregatingActivities;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.service.AchievementCompletionCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

class MilestoneCompletionUpdateServiceTest {
    private final UserId userId = new UserId(42L);
    private final MilestoneId milestoneId = new MilestoneId(23L);

    private ApplicationEventPublisher eventPublisher;
    private ForAccessingMilestones forAccessingMilestones;
    private ForAggregatingActivities forAggregatingActivities;
    private AchievementCompletionCalculator achievementCompletionCalculator;
    private TrophyManagementService trophyManagementService;
    private MilestoneCompletionUpdateService service;

    private Milestone buildMilestoneWithCompleted(boolean completed) {
        return Milestone
            .builder()
            .id(milestoneId)
            .ulid(new MilestoneUlid("TESTULID000000000000000001"))
            .name("Milestone Name")
            .description("Milestone Desc")
            .imageFilePath("milestone.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(10_000L)
            .isCompleted(completed)
            .build();
    }

    @BeforeEach
    void setUp() {
        eventPublisher = mock(ApplicationEventPublisher.class);
        forAccessingMilestones = mock(ForAccessingMilestones.class);
        forAggregatingActivities = mock(ForAggregatingActivities.class);
        achievementCompletionCalculator = mock(AchievementCompletionCalculator.class);
        trophyManagementService = mock(TrophyManagementService.class);

        service = new MilestoneCompletionUpdateService(
            eventPublisher,
            forAccessingMilestones,
            forAggregatingActivities,
            achievementCompletionCalculator,
            trophyManagementService
        );

        // Aggregation data is irrelevant because the calculator is mocked
        when(forAggregatingActivities.aggregateForUser(any()))
            .thenReturn(ActivityAggregationMap.create(
                ActivityAggregation.zero(),
                List.of()
            ));
    }

    @Test
    void updateAllMilestoneCompletionsForUser_whenBecameComplete_publishesEvent_andCreatesTrophy() {
        Milestone milestone = buildMilestoneWithCompleted(false);
        when(forAccessingMilestones.findAllWithCompletedByUser(userId)).thenReturn(List.of(milestone));
        when(achievementCompletionCalculator.calculateAchievementCompletion(any()))
            .thenReturn(AchievementCompletionResult
                .builder()
                .percentageCompleted(100)
                .becameComplete(true)
                .becameIncomplete(false)
                .build());

        service.updateAllMilestoneCompletionsForUser(userId);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(MilestoneCompletedEvent.class, event);
        MilestoneCompletedEvent milestoneCompletedEvent = (MilestoneCompletedEvent) event;
        assertEquals(userId, milestoneCompletedEvent.userId());
        assertEquals(milestoneId, milestoneCompletedEvent.milestoneId());

        verify(trophyManagementService).createTrophyIfNotExists(userId, milestoneId);
        verify(trophyManagementService, never()).deleteTrophyIfExists(any(), any(MilestoneId.class));
    }

    @Test
    void updateAllMilestoneCompletionsForUser_whenBecameIncomplete_publishesEvent_andDeletesTrophy() {
        Milestone milestone = buildMilestoneWithCompleted(true);
        when(forAccessingMilestones.findAllWithCompletedByUser(userId)).thenReturn(List.of(milestone));
        when(achievementCompletionCalculator.calculateAchievementCompletion(any()))
            .thenReturn(AchievementCompletionResult
                .builder()
                .percentageCompleted(80)
                .becameComplete(false)
                .becameIncomplete(true)
                .build());

        service.updateAllMilestoneCompletionsForUser(userId);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(MilestoneBecameIncompleteEvent.class, event);
        MilestoneBecameIncompleteEvent milestoneBecameIncompleteEvent = (MilestoneBecameIncompleteEvent) event;
        assertEquals(userId, milestoneBecameIncompleteEvent.userId());
        assertEquals(milestoneId, milestoneBecameIncompleteEvent.milestoneId());

        verify(trophyManagementService).deleteTrophyIfExists(userId, milestoneId);
        verify(trophyManagementService, never()).createTrophyIfNotExists(any(), any());
    }

    @Test
    void updateAllMilestoneCompletionsForUser_whenNoTransition_publishesNoEvents_orTrophyActions() {
        Milestone milestone = buildMilestoneWithCompleted(false);
        when(forAccessingMilestones.findAllWithCompletedByUser(userId)).thenReturn(List.of(milestone));
        when(achievementCompletionCalculator.calculateAchievementCompletion(any()))
            .thenReturn(AchievementCompletionResult
                .builder()
                .percentageCompleted(10)
                .becameComplete(false)
                .becameIncomplete(false)
                .build());

        service.updateAllMilestoneCompletionsForUser(userId);

        verifyNoInteractions(trophyManagementService);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void updateAllMilestoneCompletionsForUser_delegatesForEachMilestone_andAggregatesOnce() {
        when(forAccessingMilestones.findAllWithCompletedByUser(userId)).thenReturn(List.of(
            buildMilestoneWithCompleted(false),
            buildMilestoneWithCompleted(true)
        ));
        when(achievementCompletionCalculator.calculateAchievementCompletion(any()))
            .thenReturn(AchievementCompletionResult
                .builder()
                .percentageCompleted(23)
                .becameComplete(false)
                .becameIncomplete(false)
                .build());

        service.updateAllMilestoneCompletionsForUser(userId);

        verify(forAccessingMilestones).findAllWithCompletedByUser(userId);
        verify(forAggregatingActivities, times(1)).aggregateForUser(userId);
        verify(achievementCompletionCalculator, times(2)).calculateAchievementCompletion(any());
        verifyNoInteractions(eventPublisher);
        verifyNoInteractions(trophyManagementService);
    }
}
