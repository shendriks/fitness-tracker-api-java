package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper.ActivityDbEntityMapper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ActivityDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.application.exception.ActivityNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.exception.UserNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingActivities;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-based adapter that implements ForAccessingActivities to persist and query Activity aggregates.
 *
 * <p>Provides CRUD-style operations for activities scoped to a user using Spring Data repositories and mappers.</p>
 */
@Repository
@AllArgsConstructor
public class ActivityJpaRepositoryAdapter implements ForAccessingActivities {
    private final ActivityDbEntityRepository activityDbEntityRepository;
    private final ActivityDbEntityMapper activityMapper;
    private final UserRepository userRepository;

    /**
     * Counts all activities belonging to the given user.
     *
     * @param userId the owner of the activities
     * @return number of activities
     */
    @Override
    public long countByUser(UserId userId) {
        return activityDbEntityRepository.countByUserId(userId.value());
    }

    /**
     * Retrieves all activities for the specified user, ordered by newest first.
     *
     * @param userId the user whose activities to fetch
     * @return list of domain activities
     */
    @Override
    public List<Activity> findAllByUser(UserId userId) {
        List<ActivityDbEntity> entities = activityDbEntityRepository.findAllByUserIdOrderByUlidDesc(userId.value());
        return activityMapper.toActivities(entities);
    }

    /**
     * Finds detailed activity information by user and activity ULID.
     *
     * @param userId the owner of the activity
     * @param activityUlid the ULID of the activity
     * @return optional activity details if found
     */
    @Override
    public Optional<ActivityDetails> findByUserAndId(UserId userId, ActivityUlid activityUlid) {
        var entity = activityDbEntityRepository.findByUserIdAndUlid(userId.value(), activityUlid.value());
        return entity.map(activityMapper::toActivityDetails);
    }

    /**
     * Persists a new activity for the given user from creation data and computed average speed.
     *
     * @param userId the owner of the new activity
     * @param activityCreationData source data for the activity
     * @param averageSpeed the computed average speed to store
     * @return the saved activity details
     * @throws UserNotFoundException if the user does not exist
     */
    @Override
    public ActivityDetails saveForUser(UserId userId, ActivityCreationData activityCreationData, Speed averageSpeed) {
        ActivityDbEntity entity = activityMapper.toActivityDbEntity(activityCreationData, averageSpeed);
        UserDbEntity user = userRepository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
        entity.setUser(user);
        entity = activityDbEntityRepository.save(entity);
        return activityMapper.toActivityDetails(entity);
    }

    /**
     * Updates editable fields of an existing activity for a user.
     *
     * @param userId the owner of the activity
     * @param activityUlid the ULID of the activity to update
     * @param activityUpdateData fields to update
     * @return updated activity details
     * @throws ActivityNotFoundException if the activity does not exist for the user
     */
    @Override
    public ActivityDetails updateForUser(UserId userId, ActivityUlid activityUlid, ActivityUpdateData activityUpdateData) {
        ActivityDbEntity entity = activityDbEntityRepository
            .findByUserIdAndUlid(userId.value(), activityUlid.value())
            .orElseThrow(ActivityNotFoundException::new);
        entity.setActivityType(activityUpdateData.activityType());
        entity.setTitle(activityUpdateData.title());
        entity.setDescription(activityUpdateData.description());
        entity = activityDbEntityRepository.save(entity);
        return activityMapper.toActivityDetails(entity);
    }

    /**
     * Retrieves the title of an activity by its numeric identifier.
     *
     * @param activityId the database identifier of the activity
     * @return optional title if present
     */
    @Override
    public Optional<String> findTitleById(ActivityId activityId) {
        return activityDbEntityRepository.findTitleById(activityId.value());
    }

    /**
     * Persists a new uploaded activity (including GPX-derived data and preview image) for the given user.
     *
     * @param userId the owner of the new activity
     * @param activityUploadData metadata included with the upload
     * @param gpsTrackData parsed GPS track metrics and points
     * @param imageData a generated preview image of the route
     * @return the saved activity details
     * @throws UserNotFoundException if the user does not exist
     */
    @Override
    public ActivityDetails saveForUser(
        UserId userId,
        ActivityUploadData activityUploadData,
        GPSTrackData gpsTrackData,
        ImageData imageData
    ) {
        ActivityDbEntity activityDbEntry = activityMapper.toActivityDbEntity(activityUploadData, gpsTrackData, imageData);
        activityDbEntry.setUser(userRepository.findById(userId.value()).orElseThrow(UserNotFoundException::new));
        activityDbEntityRepository.save(activityDbEntry);
        return activityMapper.toActivityDetails(activityDbEntry);
    }

    /**
     * Deletes an activity for the given user if it exists.
     *
     * @param userId the owner of the activity
     * @param activityUlid the ULID of the activity to delete
     * @throws ActivityNotFoundException if the activity doesn't exist for the user
     */
    @Override
    public void deleteForUser(UserId userId, ActivityUlid activityUlid) {
        ActivityDbEntity activityDbEntity = activityDbEntityRepository
            .findByUserIdAndUlid(userId.value(), activityUlid.value())
            .orElseThrow(ActivityNotFoundException::new);
        activityDbEntityRepository.delete(activityDbEntity);
    }
}
