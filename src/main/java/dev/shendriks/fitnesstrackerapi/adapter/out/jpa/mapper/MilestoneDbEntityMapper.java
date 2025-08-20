package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection.MilestoneProjection;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneUlid;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class MilestoneDbEntityMapper {
    public abstract List<Milestone> toMilestones(List<MilestoneProjection> projections);

    public abstract Milestone toMilestone(MilestoneProjection projection);

    MilestoneId mapMilestoneId(Long id) {
        return new MilestoneId(id);
    }

    MilestoneUlid mapMilestoneUlid(String ulid) {
        return new MilestoneUlid(ulid);
    }
}
