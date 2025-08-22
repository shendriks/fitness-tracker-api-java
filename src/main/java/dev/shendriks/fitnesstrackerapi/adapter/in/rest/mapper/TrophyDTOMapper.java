package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.TrophyResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrophyDTOMapper {
    List<TrophyResponseDTO> toTrophyResponseDTOs(List<Trophy> trophies);

    @Mapping(target = "id", source = "ulid.value")
    @Mapping(target = "achievement.id", source = "achievement.ulid.value")
    TrophyResponseDTO toTrophyResponseDTO(Trophy trophy);
}
