package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityAggregationDbProjection;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.ActivityTypeAggregationDbProjection;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregation;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityTypeAggregation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityAggregationMapper {
    ActivityAggregation toActivityAggregation(ActivityAggregationDbProjection projection);

    List<ActivityTypeAggregation> toActivityTypeAggregations(List<ActivityTypeAggregationDbProjection> projections);

    default ActivityAggregationMap toActivityAggregationMap(
        ActivityAggregationDbProjection total,
        List<ActivityTypeAggregationDbProjection> byType
    ) {
        return ActivityAggregationMap.create(
            toActivityAggregation(total),
            toActivityTypeAggregations(byType)
        );
    }
}
