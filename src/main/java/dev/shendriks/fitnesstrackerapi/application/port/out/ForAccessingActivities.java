package dev.shendriks.fitnesstrackerapi.application.port.out;

import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.*;

import java.util.List;
import java.util.Optional;

public interface ForAccessingActivities {
    long countByUser(UserId userId);

    void deleteForUser(UserId userId, ActivityUlid activityUlid);

    List<Activity> findAllByUser(UserId userId);

    Optional<Activity> findByUserAndId(UserId userId, ActivityUlid activityUlid);

    Activity saveForUser(UserId userId, ActivityCreationData activityCreationData);

    Activity saveForUser(UserId userId, ActivityUploadData activityUploadData, GPSTrackData gpsTrackData);

    Activity updateForUser(UserId userId, ActivityUlid achievementUlid, ActivityUpdateData activityUpdateData);

    Optional<String> findTitleById(ActivityId activityId);
}
