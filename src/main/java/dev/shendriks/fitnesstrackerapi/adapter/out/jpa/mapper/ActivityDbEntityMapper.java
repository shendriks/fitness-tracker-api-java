package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.GPSPositionDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ActivityDbEntityMapper {
    public abstract Activity activityDbEntityToActivity(ActivityDbEntity activityDbEntity);

    public abstract List<Activity> activityDbEntitiesToActivities(List<ActivityDbEntity> activityDbEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ulid", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "gpsPositions", ignore = true)
    @Mapping(target = "state", ignore = true)
    public abstract ActivityDbEntity activityCreationDataToActivityDbEntity(ActivityCreationData activityCreationData);

    public ActivityDbEntity activityUploadDataToActivityDbEntity(ActivityUploadData request, GPSTrackData metrics) {
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

    public ActivityId mapActivityId(Long id) {
        return new ActivityId(id);
    }

    public ActivityUlid mapActivityUlid(String ulid) {
        return new ActivityUlid(ulid);
    }
}
