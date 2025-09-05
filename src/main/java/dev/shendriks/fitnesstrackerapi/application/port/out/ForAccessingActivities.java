package dev.shendriks.fitnesstrackerapi.application.port.out;

import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.value.*;

import java.util.List;
import java.util.Optional;

public interface ForAccessingActivities {
    long countByUser(UserId userId);

    void deleteForUser(UserId userId, ActivityUlid activityUlid);

    List<Activity> findAllByUser(UserId userId);

    Optional<ActivityDetails> findByUserAndId(UserId userId, ActivityUlid activityUlid);

    ActivityDetails saveForUser(UserId userId, ActivityCreationData activityCreationData, Speed averageSpeed);

    ActivityDetails saveForUser(UserId userId, ActivityUploadData activityUploadData, GPSTrackData gpsTrackData, String imageData);

    ActivityDetails updateForUser(UserId userId, ActivityUlid achievementUlid, ActivityUpdateData activityUpdateData);

    Optional<String> findTitleById(ActivityId activityId);
}
