package dev.shendriks.fitnesstrackerapi.domain.challenge.service;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeParticipationResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.challenge.event.ChallengeJoinedEvent;
import dev.shendriks.fitnesstrackerapi.domain.challenge.event.ChallengeLeftEvent;
import dev.shendriks.fitnesstrackerapi.domain.challenge.exception.ChallengeNotFoundException;
import dev.shendriks.fitnesstrackerapi.domain.challenge.mapper.ChallengeMapper;
import dev.shendriks.fitnesstrackerapi.domain.challenge.mapper.ChallengeParticipationMapper;
import dev.shendriks.fitnesstrackerapi.domain.challenge.repository.ChallengeParticipationRepository;
import dev.shendriks.fitnesstrackerapi.domain.challenge.repository.ChallengeRepository;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import lombok.extern.java.Log;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final ChallengeMapper challengeMapper;
    private final ChallengeParticipationMapper participationMapper;
    private final ApplicationEventPublisher eventPublisher;

    public ChallengeService(
        ChallengeRepository challengeRepository,
        ChallengeParticipationRepository participationRepository,
        ChallengeMapper challengeMapper,
        ChallengeParticipationMapper participationMapper,
        ApplicationEventPublisher eventPublisher
    ) {
        this.challengeRepository = challengeRepository;
        this.participationRepository = participationRepository;
        this.challengeMapper = challengeMapper;
        this.participationMapper = participationMapper;
        this.eventPublisher = eventPublisher;
    }

    public Iterable<ChallengeResponse> getAllChallenges(User user) {
        Iterable<Challenge> challenges = challengeRepository.findAll();

        return challengeMapper.toResponses(challenges, user);
    }

    public void joinChallenge(String challengeId, User user) {
        Challenge challenge = findChallengeOrElseThrow(challengeId);

        if (this.participationRepository.existsByChallengeAndUser(challenge, user)) {
            return;
        }

        ChallengeParticipation participation = new ChallengeParticipation();
        participation.setChallenge(challenge);
        participation.setUser(user);
        this.participationRepository.save(participation);
        eventPublisher.publishEvent(new ChallengeJoinedEvent(this, user.getId(), challenge.getId()));
    }

    public void leaveChallenge(String challengeId, User user) {
        Challenge challenge = findChallengeOrElseThrow(challengeId);

        Optional<ChallengeParticipation> participation = this.participationRepository.findByChallengeAndUser(challenge, user);
        participation.ifPresent(this.participationRepository::delete);
        eventPublisher.publishEvent(new ChallengeLeftEvent(this, user.getId(), challenge.getId()));
    }

    public Iterable<ChallengeParticipationResponse> getAllChallengeParticipations(User user) {
        Iterable<ChallengeParticipation> challengeParticipations = this.participationRepository.findByUser(user);

        return participationMapper.toResponses(challengeParticipations, user);
    }

    private Challenge findChallengeOrElseThrow(String challengeId) {
        return this.challengeRepository
            .findByUlid(challengeId)
            .orElseThrow(() -> new ChallengeNotFoundException(challengeId));
    }
}
