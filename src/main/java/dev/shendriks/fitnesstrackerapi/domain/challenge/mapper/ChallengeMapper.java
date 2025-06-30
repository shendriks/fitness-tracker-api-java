package dev.shendriks.fitnesstrackerapi.domain.challenge.mapper;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChallengeMapper {
    public List<ChallengeResponse> toResponses(Iterable<Challenge> challenges) {
        List<ChallengeResponse> challengeResponses = new ArrayList<>();
        for (Challenge challenge : challenges) {
            challengeResponses.add(toResponse(challenge));
        }

        return challengeResponses;
    }

    public ChallengeResponse toResponse(Challenge challenge) {
        return new ChallengeResponse(
            challenge.getUlid(),
            challenge.getName(),
            challenge.getDescription()
        );
    }
}
