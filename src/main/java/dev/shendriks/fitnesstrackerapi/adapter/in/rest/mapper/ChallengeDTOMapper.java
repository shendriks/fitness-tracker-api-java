package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ChallengeResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChallengeDTOMapper {
    @Mapping(target = "id", source = "ulid.value")
    ChallengeResponseDTO toChallengeResponse(Challenge challenge);

    List<ChallengeResponseDTO> toChallengeResponses(List<Challenge> challenges);
}
