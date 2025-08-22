package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.MilestoneResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MilestoneDTOMapper {
    List<MilestoneResponseDTO> toMilestoneResponseDTOs(List<Milestone> milestones);

    @Mapping(target = "id", source = "ulid.value")
    @Mapping(target = "isCompleted", source = "completed")
    MilestoneResponseDTO toMilestoneResponseDTO(Milestone milestone);
}
