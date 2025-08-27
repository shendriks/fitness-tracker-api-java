package dev.shendriks.fitnesstrackerapi.application.eventlistener;

import dev.shendriks.fitnesstrackerapi.application.event.*;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingActivities;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallenges;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingMilestones;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingNotifications;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCreatorListenerTest {
    @Mock
    private ForAccessingNotifications forAccessingNotifications;
    @Mock
    private ForAccessingChallenges forAccessingChallenges;
    @Mock
    private ForAccessingMilestones forAccessingMilestones;
    @Mock
    private ForAccessingActivities forAccessingActivities;
    private NotificationCreatorListener listener;

    @BeforeEach
    void setUp() {
        listener = new NotificationCreatorListener(
            forAccessingNotifications,
            forAccessingChallenges,
            forAccessingMilestones,
            forAccessingActivities
        );
    }

    @Test
    void handle_withChallengeJoinedEvent_savesNotificationWithChallengeName() {
        UserId userId = new UserId(42L);
        ChallengeUlid challengeUlid = new ChallengeUlid("TESTULID000000000000000001");
        when(forAccessingChallenges.findNameByUlid(challengeUlid)).thenReturn(Optional.of("Spring Marathon"));

        listener.handle(new ChallengeJoinedEvent(userId, challengeUlid));

        ArgumentCaptor<NotificationCreationData> captor = ArgumentCaptor.forClass(NotificationCreationData.class);
        verify(forAccessingNotifications).save(captor.capture());
        NotificationCreationData actualNotificationCreationData = captor.getValue();
        assertEquals(userId, actualNotificationCreationData.userId());
        assertEquals("Challenge joined", actualNotificationCreationData.title());
        assertEquals("You have joined the challenge \"Spring Marathon\"", actualNotificationCreationData.description());
        verify(forAccessingChallenges).findNameByUlid(challengeUlid);
        verifyNoInteractions(forAccessingMilestones, forAccessingActivities);
    }

    @Test
    void handle_withChallengeLeftEvent_savesNotificationWithChallengeName() {
        UserId userId = new UserId(42L);
        ChallengeUlid challengeUlid = new ChallengeUlid("TESTULID000000000000000002");
        when(forAccessingChallenges.findNameByUlid(challengeUlid)).thenReturn(Optional.of("Cycling 500k"));

        listener.handle(new ChallengeLeftEvent(userId, challengeUlid));

        ArgumentCaptor<NotificationCreationData> captor = ArgumentCaptor.forClass(NotificationCreationData.class);
        verify(forAccessingNotifications).save(captor.capture());
        NotificationCreationData actualNotificationCreationData = captor.getValue();
        assertEquals(userId, actualNotificationCreationData.userId());
        assertEquals("Challenge left", actualNotificationCreationData.title());
        assertEquals("You have left the challenge \"Cycling 500k\"", actualNotificationCreationData.description());
        verify(forAccessingChallenges).findNameByUlid(challengeUlid);
        verifyNoInteractions(forAccessingMilestones, forAccessingActivities);
    }

    @Test
    void handle_withChallengeCompletedEvent_savesNotificationWithChallengeName() {
        UserId userId = new UserId(42L);
        ChallengeId challengeId = new ChallengeId(23L);
        when(forAccessingChallenges.findNameById(challengeId)).thenReturn(Optional.of("Trail Run"));

        listener.handle(new ChallengeCompletedEvent(userId, challengeId));

        ArgumentCaptor<NotificationCreationData> captor = ArgumentCaptor.forClass(NotificationCreationData.class);
        verify(forAccessingNotifications).save(captor.capture());
        NotificationCreationData actualNotificationCreationData = captor.getValue();
        assertEquals(userId, actualNotificationCreationData.userId());
        assertEquals("Wow, congratulations!", actualNotificationCreationData.title());
        assertEquals("You have completed the challenge Trail Run", actualNotificationCreationData.description());
        verify(forAccessingChallenges).findNameById(challengeId);
        verifyNoInteractions(forAccessingMilestones, forAccessingActivities);
    }

    @Test
    void handle_withMilestoneCompletedEvent_savesNotificationWithMilestoneName() {
        UserId userId = new UserId(42L);
        MilestoneId milestoneId = new MilestoneId(23L);
        when(forAccessingMilestones.findNameById(milestoneId)).thenReturn(Optional.of("Halfway There"));

        listener.handle(new MilestoneCompletedEvent(userId, milestoneId));

        ArgumentCaptor<NotificationCreationData> captor = ArgumentCaptor.forClass(NotificationCreationData.class);
        verify(forAccessingNotifications).save(captor.capture());
        NotificationCreationData actualNotificationCreationData = captor.getValue();
        assertEquals(userId, actualNotificationCreationData.userId());
        assertEquals("Another milestone reached!", actualNotificationCreationData.title());
        assertEquals("You have completed the milestone Halfway There", actualNotificationCreationData.description());
        verify(forAccessingMilestones).findNameById(milestoneId);
        verifyNoInteractions(forAccessingChallenges, forAccessingActivities);
    }

    @Test
    void handle_withActivitySavedEvent_savesNotificationWithActivityTitle() {
        UserId userId = new UserId(42L);
        ActivityId activityId = new ActivityId(13L);
        when(forAccessingActivities.findTitleById(activityId)).thenReturn(Optional.of("Morning Ride"));

        listener.handle(new ActivitySavedEvent(userId, activityId));

        ArgumentCaptor<NotificationCreationData> captor = ArgumentCaptor.forClass(NotificationCreationData.class);
        verify(forAccessingNotifications).save(captor.capture());
        NotificationCreationData actualNotificationCreationData = captor.getValue();
        assertEquals(userId, actualNotificationCreationData.userId());
        assertEquals("Well done!", actualNotificationCreationData.title());
        assertEquals("You have saved a new activity: Morning Ride", actualNotificationCreationData.description());
        verify(forAccessingActivities).findTitleById(activityId);
        verifyNoInteractions(forAccessingChallenges, forAccessingMilestones);
    }

    @Test
    void handle_withActivityUpdatedEvent_savesNotificationWithActivityTitle() {
        UserId userId = new UserId(42L);
        ActivityId activityId = new ActivityId(13L);
        when(forAccessingActivities.findTitleById(activityId)).thenReturn(Optional.of("Evening Run"));

        listener.handle(new ActivityUpdatedEvent(userId, activityId));

        ArgumentCaptor<NotificationCreationData> captor = ArgumentCaptor.forClass(NotificationCreationData.class);
        verify(forAccessingNotifications).save(captor.capture());
        NotificationCreationData actualNotificationCreationData = captor.getValue();
        assertEquals(userId, actualNotificationCreationData.userId());
        assertEquals("Activity updated!", actualNotificationCreationData.title());
        assertEquals("You have updated an activity: Evening Run", actualNotificationCreationData.description());
        verify(forAccessingActivities).findTitleById(activityId);
        verifyNoInteractions(forAccessingChallenges, forAccessingMilestones);
    }

    @Test
    void handle_withActivityDeletedEvent_savesNotificationWithoutLookup() {
        UserId userId = new UserId(42L);
        ActivityUlid activityUlid = new ActivityUlid("TESTULID000000000000000003");

        listener.handle(new ActivityDeletedEvent(userId, activityUlid));

        ArgumentCaptor<NotificationCreationData> captor = ArgumentCaptor.forClass(NotificationCreationData.class);
        verify(forAccessingNotifications).save(captor.capture());
        NotificationCreationData actualNotificationCreationData = captor.getValue();
        assertEquals(userId, actualNotificationCreationData.userId());
        assertEquals("Bummer!", actualNotificationCreationData.title());
        assertEquals("You have deleted an activity", actualNotificationCreationData.description());
        verifyNoInteractions(forAccessingActivities, forAccessingChallenges, forAccessingMilestones);
    }

    @Test
    void handle_withTrophyUnlockedEvent_savesNotification() {
        UserId userId = new UserId(42L);
        TrophyId trophyId = new TrophyId(37L);

        listener.handle(new TrophyUnlockedEvent(userId, trophyId));

        ArgumentCaptor<NotificationCreationData> captor = ArgumentCaptor.forClass(NotificationCreationData.class);
        verify(forAccessingNotifications).save(captor.capture());
        NotificationCreationData actualNotificationCreationData = captor.getValue();
        assertEquals(userId, actualNotificationCreationData.userId());
        assertEquals("Superb!", actualNotificationCreationData.title());
        assertEquals("You have unlocked a trophy", actualNotificationCreationData.description());
        verifyNoInteractions(forAccessingActivities, forAccessingChallenges, forAccessingMilestones);
    }

    @Test
    void handle_withTrophyLostEvent_savesNotification() {
        UserId userId = new UserId(42L);

        listener.handle(new TrophyLostEvent(userId));

        ArgumentCaptor<NotificationCreationData> captor = ArgumentCaptor.forClass(NotificationCreationData.class);
        verify(forAccessingNotifications).save(captor.capture());
        NotificationCreationData actualNotificationCreationData = captor.getValue();
        assertEquals(userId, actualNotificationCreationData.userId());
        assertEquals("D'oh!", actualNotificationCreationData.title());
        assertEquals("You have lost a trophy", actualNotificationCreationData.description());
        verifyNoInteractions(forAccessingActivities, forAccessingChallenges, forAccessingMilestones);
    }
}
