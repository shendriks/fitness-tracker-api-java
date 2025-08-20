package dev.shendriks.fitnesstrackerapi.adapter.in.rest.activity.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.activity.dto.*;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityCreationData;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUpdateData;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUploadData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityDTOMapper {
    @Mapping(target = "id", source = "ulid.value")
    ActivityResponseDTO activityToActivityResponse(Activity activity);

    @Mapping(target = "id", source = "ulid.value")
    List<ActivityResponseDTO> activitiesToActivityResponses(List<Activity> activities);

    @Mapping(target = "id", source = "ulid.value")
    ActivityDetailsResponseDTO activityToActivityDetailsResponse(Activity activity);

    @Mapping(target = "activityType", expression = "java(ActivityType.fromString(request.activityType()))")
    ActivityCreationData activityCreationRequestToActivityCreationData(ActivityCreateRequestDTO request);

    @Mapping(target = "activityType", expression = "java(ActivityType.fromString(request.activityType()))")
    ActivityUpdateData activityUpdateRequestToActivityUpdateData(ActivityUpdateRequestDTO request);

    @Mapping(target = "activityType", expression = "java(ActivityType.fromString(request.activityType()))")
    ActivityUploadData activityUploadRequestToActivityUploadData(ActivityUploadRequestDTO request);
}
