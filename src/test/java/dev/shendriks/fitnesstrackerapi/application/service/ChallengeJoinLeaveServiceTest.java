package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.ChallengeJoinedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ChallengeLeftEvent;
import dev.shendriks.fitnesstrackerapi.application.exception.ChallengeNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallengeParticipations;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallenges;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChallengeJoinLeaveServiceTest {
    private final UserId userId = new UserId(42L);
    private final ChallengeUlid challengeUlid = new ChallengeUlid("TESTULID000000000000000001");
    private ApplicationEventPublisher eventPublisher;
    private ForAccessingChallenges forAccessingChallenges;
    private ForAccessingChallengeParticipations forAccessingChallengeParticipations;
    private ChallengeCompletionUpdateService challengeCompletionUpdateService;
    private TrophyManagementService trophyManagementService;
    private ChallengeJoinLeaveService service;

    @BeforeEach
    void setUp() {
        eventPublisher = mock(ApplicationEventPublisher.class);
        forAccessingChallenges = mock(ForAccessingChallenges.class);
        forAccessingChallengeParticipations = mock(ForAccessingChallengeParticipations.class);
        challengeCompletionUpdateService = mock(ChallengeCompletionUpdateService.class);
        trophyManagementService = mock(TrophyManagementService.class);

        service = new ChallengeJoinLeaveService(
            eventPublisher,
            forAccessingChallenges,
            forAccessingChallengeParticipations,
            challengeCompletionUpdateService,
            trophyManagementService
        );
    }

    @Test
    void joinChallenge_whenAlreadyJoined_doesNothing() {
        when(forAccessingChallengeParticipations.existsByChallengeAndUser(userId, challengeUlid)).thenReturn(true);

        service.joinChallenge(userId, challengeUlid);

        verify(forAccessingChallengeParticipations).existsByChallengeAndUser(userId, challengeUlid);
        verifyNoMoreInteractions(forAccessingChallengeParticipations);

        verifyNoInteractions(forAccessingChallenges);
        verifyNoInteractions(challengeCompletionUpdateService);
        verifyNoInteractions(eventPublisher);
        verifyNoInteractions(trophyManagementService);
    }

    @Test
    void joinChallenge_whenChallengeFound_createsParticipation_updatesCompletion_andPublishesEvent() {
        when(forAccessingChallengeParticipations.existsByChallengeAndUser(userId, challengeUlid)).thenReturn(false);

        Challenge challenge = Challenge
            .builder()
            .id(new ChallengeId(123L))
            .ulid(challengeUlid)
            .name("Name")
            .description("Desc")
            .imageFilePath("img.png")
            .activityType(ActivityType.RUNNING)
            .activityMetric(ActivityMetric.TOTAL_DISTANCE)
            .completionThreshold(10_000L)
            .startDate(Instant.now())
            .endDate(Instant.now())
            .hasUserJoined(false)
            .build();
        when(forAccessingChallenges.findByUserIdAndUlid(userId, challengeUlid)).thenReturn(Optional.of(challenge));

        ChallengeParticipation participation = ChallengeParticipation
            .builder()
            .id(new ChallengeParticipationId(555L))
            .ulid(new ChallengeParticipationUlid("TESTULID000000000000000002"))
            .userId(userId)
            .challenge(challenge)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .percentageCompleted(0)
            .build();
        when(forAccessingChallengeParticipations.joinChallenge(userId, challenge.getUlid())).thenReturn(participation);

        service.joinChallenge(userId, challengeUlid);

        verify(forAccessingChallenges).findByUserIdAndUlid(userId, challengeUlid);
        verify(forAccessingChallengeParticipations).joinChallenge(userId, challenge.getUlid());
        verify(challengeCompletionUpdateService).updateChallengeCompletion(participation);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(ChallengeJoinedEvent.class, event);
        ChallengeJoinedEvent challengeJoinedEvent = (ChallengeJoinedEvent) event;
        assertEquals(userId, challengeJoinedEvent.userId());
        assertEquals(challengeUlid, challengeJoinedEvent.challengeUlid());
    }

    @Test
    void joinChallenge_whenChallengeMissing_throwsNotFound_andDoesNothingElse() {
        when(forAccessingChallengeParticipations.existsByChallengeAndUser(userId, challengeUlid)).thenReturn(false);
        when(forAccessingChallenges.findByUserIdAndUlid(userId, challengeUlid)).thenReturn(Optional.empty());

        assertThrows(ChallengeNotFoundException.class, () -> service.joinChallenge(userId, challengeUlid));

        verify(forAccessingChallenges).findByUserIdAndUlid(userId, challengeUlid);
        verify(forAccessingChallengeParticipations, never()).joinChallenge(any(), any());
        verifyNoInteractions(challengeCompletionUpdateService);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void leaveChallenge_whenNotJoined_doesNothing() {
        when(forAccessingChallengeParticipations.existsByChallengeAndUser(userId, challengeUlid)).thenReturn(false);

        service.leaveChallenge(userId, challengeUlid);

        verify(forAccessingChallengeParticipations, never()).leaveChallenge(any(), any());
        verifyNoInteractions(trophyManagementService);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void leaveChallenge_whenJoined_leaves_deletesTrophy_andPublishesEvent() {
        when(forAccessingChallengeParticipations.existsByChallengeAndUser(userId, challengeUlid)).thenReturn(true);

        service.leaveChallenge(userId, challengeUlid);

        verify(forAccessingChallengeParticipations).leaveChallenge(userId, challengeUlid);
        verify(trophyManagementService).deleteTrophyIfExists(userId, challengeUlid);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(ChallengeLeftEvent.class, event);
        ChallengeLeftEvent challengeLeftEvent = (ChallengeLeftEvent) event;
        assertEquals(userId, challengeLeftEvent.userId());
        assertEquals(challengeUlid, challengeLeftEvent.challengeUlid());
    }
}
