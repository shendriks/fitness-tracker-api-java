package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.ChallengeJoinedEvent;
import dev.shendriks.fitnesstrackerapi.application.event.ChallengeLeftEvent;
import dev.shendriks.fitnesstrackerapi.application.exception.ChallengeNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.in.challenge.JoinChallengeUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.in.challenge.LeaveChallengeUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallengeParticipations;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingChallenges;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class ChallengeJoinLeaveService implements LeaveChallengeUseCase, JoinChallengeUseCase {
    private final ApplicationEventPublisher eventPublisher;
    private final ForAccessingChallenges forAccessingChallenges;
    private final ForAccessingChallengeParticipations forAccessingChallengeParticipations;
    private final ChallengeCompletionChecker challengeCompletionChecker;
    private final TrophyManagementService trophyManagementService;

    @Override
    public void joinChallenge(UserId userId, ChallengeUlid challengeUlid) {
        if (forAccessingChallengeParticipations.existsByChallengeAndUser(userId, challengeUlid)) {
            return;
        }
        
        Challenge challenge = forAccessingChallenges
            .findByUlid(challengeUlid)
            .orElseThrow(() -> new ChallengeNotFoundException(challengeUlid.getValue()));
        
        ChallengeParticipation challengeParticipation = ChallengeParticipation.createNew(userId, challenge);
        forAccessingChallengeParticipations.create(challengeParticipation);
        challengeCompletionChecker.updateChallengeCompletion(challengeParticipation);
        
        eventPublisher.publishEvent(new ChallengeJoinedEvent(this, userId, challengeUlid));
    }

    @Override
    public void leaveChallenge(UserId userId, ChallengeUlid challengeUlid) {
        if (!forAccessingChallengeParticipations.existsByChallengeAndUser(userId, challengeUlid)) {
            return;
        }
        
        forAccessingChallengeParticipations.leaveChallenge(userId, challengeUlid);
        trophyManagementService.deleteTrophyIfExists(userId, challengeUlid);
        
        eventPublisher.publishEvent(new ChallengeLeftEvent(this, userId, challengeUlid));
    }
}
