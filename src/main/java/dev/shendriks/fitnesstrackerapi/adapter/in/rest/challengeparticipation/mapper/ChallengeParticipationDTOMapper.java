package dev.shendriks.fitnesstrackerapi.adapter.in.rest.challengeparticipation.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.challenge.mapper.ChallengeDTOMapper;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.challengeparticipation.dto.ChallengeParticipationResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ChallengeDTOMapper.class)
public interface ChallengeParticipationDTOMapper {
    List<ChallengeParticipationResponseDTO> toChallengeParticipationResponseDTOs(List<ChallengeParticipation> participations);

    @Mapping(target = "id", source = "ulid.value")
    @Mapping(target = "joinedAt", source = "createdAt")
    ChallengeParticipationResponseDTO toChallengeParticipationResponseDTO(ChallengeParticipation participation);
}
