package dev.shendriks.fitnesstrackerapi.domain.challenge.mapper;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
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
        Optional<String> imageData = Optional.empty();
        try {
            File resource = new ClassPathResource(challenge.getImageFilePath()).getFile();
            imageData = Optional.of(Base64.getEncoder().encodeToString(Files.readAllBytes(resource.toPath())));
        } catch (IOException e) {             
            log.error("Failed to read image file: %s".formatted(e.getMessage()));
        }
        
        return new ChallengeResponse(
            challenge.getUlid(),
            challenge.getName(),
            challenge.getDescription(),
            imageData
        );
    }
}
