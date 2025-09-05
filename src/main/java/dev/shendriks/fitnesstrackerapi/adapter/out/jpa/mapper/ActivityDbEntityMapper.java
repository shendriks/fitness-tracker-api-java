package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.GPSPositionDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.KilometerSpeedDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import lombok.Setter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.SETTER,
    imports = {Distance.class, Duration.class}
)
public abstract class ActivityDbEntityMapper {
    @Setter(onMethod_ = {@Autowired})
    private Clock clock;

    public abstract ActivityDetails toActivityDetails(ActivityDbEntity activityDbEntity);

    public abstract Activity toActivity(ActivityDbEntity activityDbEntity);

    public abstract List<Activity> toActivities(List<ActivityDbEntity> activityDbEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ulid", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "gpsPositions", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "averageSpeed", source = "averageSpeed")
    @Mapping(target = "kilometerSpeeds", ignore = true)
    @Mapping(target = "elevationGain", ignore = true)
    @Mapping(target = "motionTime", ignore = true)
    @Mapping(target = "pausingTime", ignore = true)
    @Mapping(target = "imagePreviewData", ignore = true)
    public abstract ActivityDbEntity toActivityDbEntity(ActivityCreationData activityCreationData, Speed averageSpeed);

    public ActivityDbEntity toActivityDbEntity(ActivityUploadData request, GPSTrackData gpsTrackData) {
        ActivityDbEntity activity = new ActivityDbEntity();
        activity.setActivityType(request.activityType());
        activity.setTitle(request.title());
        activity.setDescription(request.description());
        activity.setStartDate(gpsTrackData.gpxTime().orElse(Instant.now(clock)));
        activity.setDuration(gpsTrackData.duration());
        activity.setDistance(gpsTrackData.distance());
        activity.setAverageSpeed(gpsTrackData.speed());
        activity.setElevationGain(gpsTrackData.elevationGain());
        activity.setMotionTime(gpsTrackData.motionTime());
        activity.setPausingTime(gpsTrackData.pausingTime());
        activity.setGpsPositions(
            gpsTrackData
                .gpsPositions()
                .stream()
                .map(gpsPositionData -> GPSPositionDbEntity
                    .builder()
                    .latitude(gpsPositionData.latitude())
                    .longitude(gpsPositionData.longitude())
                    .timestamp(gpsPositionData.timestamp())
                    .altitude(gpsPositionData.altitude().orElse(null))
                    .build())
                .peek((position) -> position.setActivity(activity))
                .toList()
        );
        activity.setKilometerSpeeds(
            gpsTrackData
                .kilometerSpeeds()
                .stream()
                .map(speed -> KilometerSpeedDbEntity
                    .builder()
                    .speed(speed)
                    .build())
                .peek((speedDbEntity) -> speedDbEntity.setActivity(activity))
                .toList()
        );
        return activity;
    }

    public ActivityId mapActivityId(Long id) {
        return new ActivityId(id);
    }

    public Speed mapKilometerSpeed(KilometerSpeedDbEntity kilometerSpeedDbEntity) {
        return kilometerSpeedDbEntity.getSpeed();
    }

    public ActivityUlid mapActivityUlid(String ulid) {
        return new ActivityUlid(ulid);
    }
}
