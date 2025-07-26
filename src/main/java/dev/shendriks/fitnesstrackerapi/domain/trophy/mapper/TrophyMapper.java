package dev.shendriks.fitnesstrackerapi.domain.trophy.mapper;

import dev.shendriks.fitnesstrackerapi.domain.achievement.mapper.AchievementMapper;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.milestone.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.trophy.dto.TrophyResponse;
import dev.shendriks.fitnesstrackerapi.domain.trophy.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.trophy.enums.AchievementType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class TrophyMapper {
    private final AchievementMapper achievementMapper;

    public TrophyMapper(AchievementMapper achievementMapper) {
        this.achievementMapper = achievementMapper;
    }

    private static AchievementType getAchievementType(Trophy trophy) {
        return switch (trophy.getAchievement()) {
            case Milestone ignored -> AchievementType.MILESTONE;
            case Challenge ignored -> AchievementType.CHALLENGE;
            case null, default ->
                throw new IllegalArgumentException("Unkown achievement type: %s".formatted(Objects.requireNonNull(trophy.getAchievement()).getClass().getSimpleName()));
        };
    }

    public List<TrophyResponse> toResponses(Iterable<Trophy> trophies) {
        List<TrophyResponse> activityResponses = new ArrayList<>();
        for (Trophy trophy : trophies) {
            activityResponses.add(toResponse(trophy));
        }

        return activityResponses;
    }

    public TrophyResponse toResponse(Trophy trophy) {
        AchievementType achievementType = getAchievementType(trophy);

        return new TrophyResponse(
            trophy.getUlid(),
            achievementType,
            achievementMapper.toResponse(trophy.getAchievement()),
            trophy.getCreatedAt()
        );
    }
}
