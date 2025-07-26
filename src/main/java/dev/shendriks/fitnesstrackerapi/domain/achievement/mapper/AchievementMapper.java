package dev.shendriks.fitnesstrackerapi.domain.achievement.mapper;

import dev.shendriks.fitnesstrackerapi.domain.achievement.dto.AchievementResponse;
import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
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
public class AchievementMapper {
    public List<AchievementResponse> toResponses(Iterable<Achievement> achievements) {
        List<AchievementResponse> AchievementResponses = new ArrayList<>();
        for (Achievement achievement : achievements) {
            AchievementResponses.add(toResponse(achievement));
        }

        return AchievementResponses;
    }

    public AchievementResponse toResponse(Achievement achievement) {
        Optional<String> imageData = Optional.empty();
        try {
            File resource = new ClassPathResource(achievement.getImageFilePath()).getFile();
            imageData = Optional.of(Base64.getEncoder().encodeToString(Files.readAllBytes(resource.toPath())));
        } catch (IOException e) {
            log.error("Failed to read image file: %s".formatted(e.getMessage()));
        }

        return new AchievementResponse(
            achievement.getUlid(),
            achievement.getName(),
            achievement.getDescription(),
            imageData
        );
    }
}
