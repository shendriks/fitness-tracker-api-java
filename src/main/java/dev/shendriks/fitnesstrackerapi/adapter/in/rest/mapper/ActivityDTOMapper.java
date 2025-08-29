package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.*;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {Duration.class, Distance.class})
public interface ActivityDTOMapper {
    @Mapping(target = "id", source = "ulid.value")
    @Mapping(target = "duration", expression = "java(activity.duration().toSeconds())")
    @Mapping(target = "distance", expression = "java(activity.distance().toMeters())")
    ActivityResponseDTO toActivityResponse(Activity activity);

    @Mapping(target = "id", source = "ulid.value")
    List<ActivityResponseDTO> toActivityResponses(List<Activity> activities);

    @Mapping(target = "id", source = "ulid.value")
    @Mapping(target = "duration", expression = "java(activity.duration().toSeconds())")
    @Mapping(target = "distance", expression = "java(activity.distance().toMeters())")
    ActivityDetailsResponseDTO toActivityDetailsResponse(Activity activity);

    @Mapping(target = "activityType", expression = "java(ActivityType.fromString(request.activityType()))")
    @Mapping(target = "duration", expression = "java(Duration.ofSeconds(request.duration()))")
    @Mapping(target = "distance", expression = "java(Distance.ofMeters(request.distance()))")
    ActivityCreationData toActivityCreationData(ActivityCreateRequestDTO request);

    @Mapping(target = "activityType", expression = "java(ActivityType.fromString(request.activityType()))")
    ActivityUpdateData toActivityUpdateData(ActivityUpdateRequestDTO request);

    @Mapping(target = "activityType", expression = "java(ActivityType.fromString(request.activityType()))")
    ActivityUploadData toActivityUploadData(ActivityUploadRequestDTO request);
}
