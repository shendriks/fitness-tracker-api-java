package dev.shendriks.fitnesstrackerapi.domain.achievement.service;

import dev.shendriks.fitnesstrackerapi.domain.achievement.dto.AchievementResponse;
import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.achievement.mapper.AchievementMapper;
import dev.shendriks.fitnesstrackerapi.domain.achievement.repository.AchievementRepository;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class AchievementService {
    private final AchievementRepository repository;
    private final AchievementMapper mapper;

    public AchievementService(AchievementRepository repository, AchievementMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Iterable<AchievementResponse> getAllAchievementsByUser(User user) {
        Iterable<Achievement> achievements = repository.findAllByUser(user);

        return mapper.toResponses(achievements);
    }
}
