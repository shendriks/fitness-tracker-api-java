package dev.shendriks.fitnesstrackerapi.domain.challenge.mapper;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeParticipationResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.ChallengeParticipation;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log
@Component
public class ChallengeParticipationMapper {
    private final ChallengeMapper challengeMapper;

    public ChallengeParticipationMapper(ChallengeMapper challengeMapper) {
        this.challengeMapper = challengeMapper;
    }

    public List<ChallengeParticipationResponse> toResponses(
        Iterable<ChallengeParticipation> challengeParticipations,
        User user
    ) {
        List<ChallengeParticipationResponse> challengeParticipationResponses = new ArrayList<>();
        for (ChallengeParticipation challengeParticipation : challengeParticipations) {
            challengeParticipationResponses.add(toResponse(challengeParticipation, user));
        }

        return challengeParticipationResponses;
    }

    public ChallengeParticipationResponse toResponse(ChallengeParticipation challengeParticipation, User user) {
        return new ChallengeParticipationResponse(
            challengeParticipation.getUlid(),
            challengeMapper.toResponse(challengeParticipation.getChallenge(), user),
            challengeParticipation.getCreatedAt(),
            challengeParticipation.getPercentageCompleted()
        );
    }
}
