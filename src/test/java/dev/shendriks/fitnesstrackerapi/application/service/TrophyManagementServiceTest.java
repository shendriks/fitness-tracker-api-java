package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.TrophyLostEvent;
import dev.shendriks.fitnesstrackerapi.application.event.TrophyUnlockedEvent;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingTrophies;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

class TrophyManagementServiceTest {
    private final UserId userId = new UserId(7L);
    private final MilestoneId achievementId = new MilestoneId(123L);
    private final MilestoneUlid achievementUlid = new MilestoneUlid("TESTULID000000000000000001");
    private ApplicationEventPublisher eventPublisher;
    private ForAccessingTrophies forAccessingTrophies;
    private TrophyManagementService service;

    private static Trophy buildTrophy(UserId userId, TrophyId trophyId) {
        return Trophy
            .builder()
            .id(trophyId)
            .ulid(new TrophyUlid("TESTULID000000000000000002"))
            .userId(userId)
            .unlockedAt(Instant.now())
            .build();
    }

    @BeforeEach
    void setUp() {
        eventPublisher = mock(ApplicationEventPublisher.class);
        forAccessingTrophies = mock(ForAccessingTrophies.class);
        service = new TrophyManagementService(eventPublisher, forAccessingTrophies);
    }

    @Test
    void createTrophyIfNotExists_withAlreadyExists_doesNothing() {
        when(forAccessingTrophies.existsByUserAndAchievement(userId, achievementId)).thenReturn(true);

        service.createTrophyIfNotExists(userId, achievementId);

        verify(forAccessingTrophies).existsByUserAndAchievement(userId, achievementId);
        verify(forAccessingTrophies, never()).createTrophyForUserAndAchievement(any(), any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void createTrophyIfNotExists_withNotExists_createsTrophyAndPublishesUnlockedEvent() {
        when(forAccessingTrophies.existsByUserAndAchievement(userId, achievementId)).thenReturn(false);
        TrophyId createdTrophyId = new TrophyId(999L);
        Trophy created = buildTrophy(userId, createdTrophyId);
        when(forAccessingTrophies.createTrophyForUserAndAchievement(userId, achievementId)).thenReturn(created);

        service.createTrophyIfNotExists(userId, achievementId);

        verify(forAccessingTrophies).createTrophyForUserAndAchievement(userId, achievementId);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(TrophyUnlockedEvent.class, event);
        TrophyUnlockedEvent trophyUnlockedEvent = (TrophyUnlockedEvent) event;
        assertEquals(userId, trophyUnlockedEvent.userId());
        assertEquals(createdTrophyId, trophyUnlockedEvent.trophyId());
    }

    @Test
    void deleteTrophyIfExists_withAchievementId_deletesAndPublishesLostEvent() {
        service.deleteTrophyIfExists(userId, achievementId);

        verify(forAccessingTrophies).deleteIfNotExistsByUserAndAchievement(userId, achievementId);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(TrophyLostEvent.class, event);
        TrophyLostEvent trophyLostEvent = (TrophyLostEvent) event;
        assertEquals(userId, trophyLostEvent.userId());
    }

    @Test
    void deleteTrophyIfExists_withAchievementUlid_deletesAndPublishesLostEvent() {
        service.deleteTrophyIfExists(userId, achievementUlid);

        verify(forAccessingTrophies).deleteIfNotExistsByUserAndAchievement(userId, achievementUlid);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(TrophyLostEvent.class, event);
        TrophyLostEvent trophyLostEvent = (TrophyLostEvent) event;
        assertEquals(userId, trophyLostEvent.userId());
    }
}
