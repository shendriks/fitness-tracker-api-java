package dev.shendriks.fitnesstrackerapi.domain.milestone.service;

import dev.shendriks.fitnesstrackerapi.domain.milestone.dto.MilestoneResponse;
import dev.shendriks.fitnesstrackerapi.domain.milestone.mapper.MilestoneMapper;
import dev.shendriks.fitnesstrackerapi.domain.milestone.repository.MilestoneRepository;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class MilestoneService {
    private final MilestoneRepository repository;
    private final MilestoneMapper mapper;

    public MilestoneService(MilestoneRepository repository, MilestoneMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Iterable<MilestoneResponse> findAllByUser(User user) {
        var milestones = repository.findAllWithCompletedByUser(user);
        return mapper.toResponses(milestones);
    }
}
