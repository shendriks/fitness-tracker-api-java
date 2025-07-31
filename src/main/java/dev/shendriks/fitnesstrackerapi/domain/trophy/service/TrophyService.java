package dev.shendriks.fitnesstrackerapi.domain.trophy.service;

import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.trophy.dto.TrophyResponse;
import dev.shendriks.fitnesstrackerapi.domain.trophy.entity.Trophy;
import dev.shendriks.fitnesstrackerapi.domain.trophy.event.TrophyLostEvent;
import dev.shendriks.fitnesstrackerapi.domain.trophy.event.TrophyUnlockedEvent;
import dev.shendriks.fitnesstrackerapi.domain.trophy.mapper.TrophyMapper;
import dev.shendriks.fitnesstrackerapi.domain.trophy.repository.TrophyRepository;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrophyService {
    private final TrophyRepository repository;
    private final TrophyMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    public TrophyService(TrophyRepository repository, TrophyMapper mapper, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    public Iterable<TrophyResponse> getAllTrophiesByUser(User user) {
        Iterable<Trophy> trophies = repository.findAllByUserOrderByCreatedAtDesc(user);

        return mapper.toResponses(trophies);
    }
    
    public void createTrophyIfNotExists(User user, Achievement achievement) {
        Optional<Trophy> existingTrophy = repository.findByUserAndAchievement(user, achievement);
        if (existingTrophy.isPresent()) {
            return;
        }
        
        Trophy trophy = new Trophy();
        trophy.setUser(user);
        trophy.setAchievement(achievement);
        repository.save(trophy);
        eventPublisher.publishEvent(new TrophyUnlockedEvent(this, user.getId(), trophy.getId()));
    }

    public void deleteTrophyIfExists(User user, Achievement achievement) {
        Optional<Trophy> trophy = repository.findByUserAndAchievement(user, achievement);
        if (trophy.isEmpty()) {
            return;
        }
        repository.delete(trophy.get());
        eventPublisher.publishEvent(new TrophyLostEvent(this, user.getId(), trophy.get().getId()));
    }
}
