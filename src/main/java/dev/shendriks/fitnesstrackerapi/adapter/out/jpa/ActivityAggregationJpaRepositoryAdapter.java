package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper.ActivityAggregationMapper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityAggregationDbProjection;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityTypeAggregationDbProjection;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ActivityAggregationProjectionRepository;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAggregatingActivities;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@AllArgsConstructor
public class ActivityAggregationJpaRepositoryAdapter implements ForAggregatingActivities {
    private final ActivityAggregationProjectionRepository repository;
    private final ActivityAggregationMapper mapper;

    @Override
    public ActivityAggregationMap aggregateForUserInTimeRange(UserId userId, Instant from, Instant to) {
        ActivityAggregationDbProjection totalProjection = repository.aggregateForUserInTimeRange(userId.value(), from, to);
        List<ActivityTypeAggregationDbProjection> byTypeProjections = repository.aggregateForUserByTypeInTimeRange(userId.value(), from, to);
        return mapper.toActivityAggregationMap(totalProjection, byTypeProjections);
    }

    @Override
    public ActivityAggregationMap aggregateForUser(UserId userId) {
        ActivityAggregationDbProjection totalProjection = repository.aggregateForUser(userId.value());
        List<ActivityTypeAggregationDbProjection> byTypeProjections = repository.aggregateForUserByType(userId.value());
        return mapper.toActivityAggregationMap(totalProjection, byTypeProjections);
    }
}
