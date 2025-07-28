package dev.shendriks.fitnesstrackerapi.domain.challenge.mapper;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.challenge.repository.ChallengeParticipationRepository;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.supportive.Base64FileEncoder;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log
@Component
public class ChallengeMapper {
    private final ChallengeParticipationRepository challengeParticipationRepository;
    private final Base64FileEncoder base64FileEncoder;

    public ChallengeMapper(
        ChallengeParticipationRepository challengeParticipationRepository,
        Base64FileEncoder base64FileEncoder
    ) {
        this.challengeParticipationRepository = challengeParticipationRepository;
        this.base64FileEncoder = base64FileEncoder;
    }

    public List<ChallengeResponse> toResponses(Iterable<Challenge> challenges, User user) {
        List<ChallengeResponse> challengeResponses = new ArrayList<>();
        for (Challenge challenge : challenges) {
            challengeResponses.add(toResponse(challenge, user));
        }

        return challengeResponses;
    }

    public ChallengeResponse toResponse(Challenge challenge, User user) {
        Optional<String> imageData = base64FileEncoder.encodeFile(challenge.getImageFilePath());
        boolean hasUserJoined = this.challengeParticipationRepository.existsByChallengeAndUser(challenge, user);
        return new ChallengeResponse(
            challenge.getUlid(),
            challenge.getName(),
            challenge.getDescription(),
            challenge.getStartDate(),
            challenge.getEndDate(),
            imageData,
            hasUserJoined
        );
    }
}
