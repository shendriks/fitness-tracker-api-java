package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.GPSPositionDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityCreationData;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUploadData;
import dev.shendriks.fitnesstrackerapi.domain.value.GPSTrackData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityDbEntityMapper {
    @Mapping(target = "id", expression = "java(new ActivityId(activityDbEntity.getId()))")
    @Mapping(target = "ulid", expression = "java(new ActivityUlid(activityDbEntity.getUlid()))")
    Activity activityDbEntityToActivity(ActivityDbEntity activityDbEntity);

    List<Activity> activityDbEntitiesToActivities(List<ActivityDbEntity> activityDbEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ulid", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "gpsPositions", ignore = true)
    @Mapping(target = "state", ignore = true)
    ActivityDbEntity activityCreationDataToActivityDbEntity(ActivityCreationData activityCreationData);

    default ActivityDbEntity activityUploadDataToActivityDbEntity(ActivityUploadData request, GPSTrackData metrics) {
        ActivityDbEntity activity = new ActivityDbEntity();
        activity.setActivityType(request.activityType());
        activity.setTitle(request.title());
        activity.setDescription(request.description());
        activity.setStartDate(metrics.gpxTime().orElse(Instant.now()));
        activity.setDuration((int) metrics.duration());
        activity.setDistance((int) metrics.totalLength());
        activity.setCalories(0);
        activity.setGpsPositions(
            metrics
                .gpsPositions()
                .stream()
                .map(gpsPositionData -> {
                    GPSPositionDbEntity gpsPositionDbEntity = new GPSPositionDbEntity();
                    gpsPositionDbEntity.setLatitude(gpsPositionData.latitude());
                    gpsPositionDbEntity.setLongitude(gpsPositionData.longitude());
                    gpsPositionDbEntity.setTimestamp(gpsPositionData.timestamp());
                    return gpsPositionDbEntity;
                })
                .peek((position) -> position.setActivity(activity))
                .toList()
        );
        return activity;
    }
}
