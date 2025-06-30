package dev.shendriks.fitnesstrackerapi.domain.challenge.service;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.challenge.mapper.ChallengeMapper;
import dev.shendriks.fitnesstrackerapi.domain.challenge.repository.ChallengeRepository;
import org.springframework.stereotype.Service;

@Service
public class ChallengeService {
    private final ChallengeRepository repository;
    private final ChallengeMapper mapper;

    public ChallengeService(ChallengeRepository repository, ChallengeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Iterable<ChallengeResponse> getAllChallenges() {
        Iterable<Challenge> challenges = repository.findAll();

        return mapper.toResponses(challenges);
    }
}
