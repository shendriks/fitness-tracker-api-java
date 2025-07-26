package dev.shendriks.fitnesstrackerapi.domain.challenge.mapper;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.challenge.repository.ChallengeParticipationRepository;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import lombok.extern.java.Log;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Log
@Component
public class ChallengeMapper {
    private final ChallengeParticipationRepository challengeParticipationRepository;

    public ChallengeMapper(ChallengeParticipationRepository challengeParticipationRepository) {
        this.challengeParticipationRepository = challengeParticipationRepository;
    }

    public List<ChallengeResponse> toResponses(Iterable<Challenge> challenges, User user) {
        List<ChallengeResponse> challengeResponses = new ArrayList<>();
        for (Challenge challenge : challenges) {
            challengeResponses.add(toResponse(challenge, user));
        }

        return challengeResponses;
    }

    public ChallengeResponse toResponse(Challenge challenge, User user) {
        Optional<String> imageData = Optional.empty();
        try {
            File resource = new ClassPathResource(challenge.getImageFilePath()).getFile();
            imageData = Optional.of(Base64.getEncoder().encodeToString(Files.readAllBytes(resource.toPath())));
        } catch (IOException e) {
            log.warning("Failed to read image file: %s".formatted(e.getMessage()));
        }

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
