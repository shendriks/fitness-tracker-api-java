package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.AchievementDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.TrophyDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper.TrophyDbEntityMapper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.AchievementDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.TrophyDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingTrophies;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementId;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class TrophyJpaRepositioryAdapter implements ForAccessingTrophies {
    private final TrophyDbEntityRepository repository;
    private final AchievementDbEntityRepository achievementRepository;
    private final UserRepository userRepository;
    private final TrophyDbEntityMapper mapper;

    @Override
    public List<Trophy> findAllByUser(UserId userId) {
        List<TrophyDbEntity> trophyEntities = repository.findAllByUserIdOrderByUlidDesc(userId.value());
        return mapper.toTrophies(trophyEntities);
    }

    @Override
    public boolean existsByUserAndAchievement(UserId userId, AchievementId achievementId) {
        return repository.existsByUserIdAndAchievementId(userId.value(), achievementId.getValue());
    }

    @Override
    public Trophy createTrophyForUserAndAchievement(UserId userId, AchievementId achievementId) {
        UserDbEntity user = userRepository.findById(userId.value()).orElseThrow();
        AchievementDbEntity achievement = achievementRepository.findById(achievementId.getValue()).orElseThrow();
        TrophyDbEntity trophyDbEntity = new TrophyDbEntity();
        trophyDbEntity.setUser(user);
        trophyDbEntity.setAchievement(achievement);
        trophyDbEntity = repository.save(trophyDbEntity);
        return mapper.toTrophy(trophyDbEntity);
    }

    @Override
    public void deleteIfNotExistsByUserAndAchievement(UserId userId, AchievementId achievementId) {
        Optional<TrophyDbEntity> trophyDbEntity = repository.findByUserIdAndAchievementId(userId.value(), achievementId.getValue());
        trophyDbEntity.ifPresent(repository::delete);
    }

    @Override
    public void deleteIfNotExistsByUserAndAchievement(UserId userId, AchievementUlid achievementUlid) {
        Optional<TrophyDbEntity> trophyDbEntity = repository.findByUserIdAndAchievementUlid(userId.value(), achievementUlid.getValue());
        trophyDbEntity.ifPresent(repository::delete);
    }
}
