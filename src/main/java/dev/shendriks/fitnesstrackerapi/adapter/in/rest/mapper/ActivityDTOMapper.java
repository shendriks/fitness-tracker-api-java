package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.*;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {Duration.class, Distance.class})
public abstract class ActivityDTOMapper {
    @Mapping(target = "id", source = "ulid.value")
    @Mapping(target = "averageSpeed", expression = "java(activity.averageSpeed().toMetersPerSecond())")
    @Mapping(target = "trackPreviewImage", expression = "java(activity.trackPreviewImage() != null ? activity.trackPreviewImage().toBase64String() : null)")
    public abstract ActivityResponseDTO toActivityResponse(Activity activity);

    @Mapping(target = "id", source = "ulid.value")
    public abstract List<ActivityResponseDTO> toActivityResponses(List<Activity> activities);

    @Mapping(target = "id", source = "ulid.value")
    @Mapping(target = "averageSpeed", expression = "java(activity.averageSpeed().toMetersPerSecond())")
    @Mapping(target = "elevationGain", expression = "java(activity.elevationGain() == null ? null : activity.elevationGain().toMeters())")
    @Mapping(target = "motionTime", expression = "java(activity.motionTime() == null ? null : activity.motionTime().toSeconds())")
    @Mapping(target = "pausingTime", expression = "java(activity.pausingTime() == null ? null : activity.pausingTime().toSeconds())")
    public abstract ActivityDetailsResponseDTO toActivityDetailsResponse(ActivityDetails activity);

    @Mapping(target = "activityType", expression = "java(ActivityType.fromString(request.activityType()))")
    @Mapping(target = "duration", expression = "java(Duration.ofSeconds(request.duration()))")
    @Mapping(target = "distance", expression = "java(Distance.ofMeters(request.distance()))")
    public abstract ActivityCreationData toActivityCreationData(ActivityCreateRequestDTO request);

    @Mapping(target = "activityType", expression = "java(ActivityType.fromString(request.activityType()))")
    public abstract ActivityUpdateData toActivityUpdateData(ActivityUpdateRequestDTO request);

    @Mapping(target = "activityType", expression = "java(ActivityType.fromString(request.activityType()))")
    public abstract ActivityUploadData toActivityUploadData(ActivityUploadRequestDTO request);

    public abstract ActivityStatsResponseDTO toActivityStatsResponse(ActivityAggregationMap map);

    public Double mapDistance(Distance distance) {
        return distance != null ? distance.toMeters() : null;
    }

    public Long mapDuration(Duration duration) {
        return duration != null ? duration.toSeconds() : null;
    }

    public Double mapSpeed(Speed speed) {
        return speed != null ? speed.toMetersPerSecond() : null;
    }
}
