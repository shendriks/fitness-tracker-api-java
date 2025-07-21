package dev.shendriks.fitnesstrackerapi.domain.achievement.mapper;

import dev.shendriks.fitnesstrackerapi.domain.achievement.dto.AchievementResponse;
import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.activity.mapper.ActivityMapper;
import dev.shendriks.fitnesstrackerapi.domain.challenge.mapper.ChallengeMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AchievementMapper {
    private final ChallengeMapper challengeMapper;
    private final ActivityMapper activityMapper;

    public AchievementMapper(ChallengeMapper challengeMapper, ActivityMapper activityMapper) {
        this.challengeMapper = challengeMapper;
        this.activityMapper = activityMapper;
    }

    public List<AchievementResponse> toResponses(Iterable<Achievement> achievements) {
        List<AchievementResponse> activityResponses = new ArrayList<>();
        for (Achievement achievement : achievements) {
            activityResponses.add(toResponse(achievement));
        }

        return activityResponses;
    }

    public AchievementResponse toResponse(Achievement achievement) {
        return new AchievementResponse(
            achievement.getUlid(),
            challengeMapper.toResponse(achievement.getChallenge()),
            activityMapper.toResponse(achievement.getActivity())
        );
    }
}
