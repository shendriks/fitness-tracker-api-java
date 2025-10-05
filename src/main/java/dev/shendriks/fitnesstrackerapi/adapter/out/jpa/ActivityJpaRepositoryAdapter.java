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

@Repository
@AllArgsConstructor
public class ActivityJpaRepositoryAdapter implements ForAccessingActivities {
    private final ActivityDbEntityRepository activityDbEntityRepository;
    private final ActivityDbEntityMapper activityMapper;
    private final UserRepository userRepository;

    @Override
    public long countByUser(UserId userId) {
        return activityDbEntityRepository.countByUserId(userId.value());
    }

    @Override
    public List<Activity> findAllByUser(UserId userId) {
        List<ActivityDbEntity> entities = activityDbEntityRepository.findAllByUserIdOrderByUlidDesc(userId.value());
        return activityMapper.toActivities(entities);
    }

    @Override
    public Optional<ActivityDetails> findByUserAndId(UserId userId, ActivityUlid activityUlid) {
        var entity = activityDbEntityRepository.findByUserIdAndUlid(userId.value(), activityUlid.value());
        return entity.map(activityMapper::toActivityDetails);
    }

    @Override
    public ActivityDetails saveManualActivityForUser(UserId userId, ActivityCreationData activityCreationData, Speed averageSpeed) {
        ActivityDbEntity entity = activityMapper.toActivityDbEntity(activityCreationData, averageSpeed);
        UserDbEntity user = userRepository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
        entity.setUser(user);
        entity = activityDbEntityRepository.save(entity);
        return activityMapper.toActivityDetails(entity);
    }

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

    @Override
    public Optional<String> findTitleById(ActivityId activityId) {
        return activityDbEntityRepository.findTitleById(activityId.value());
    }

    @Override
    public ActivityDetails saveUploadedActivityForUser(
        UserId userId,
        ActivityUploadData activityUploadData,
        GPSTrackData gpsTrackData,
        ImageData trackPreview
    ) {
        ActivityDbEntity activityDbEntry = activityMapper.toActivityDbEntity(activityUploadData, gpsTrackData, trackPreview);
        activityDbEntry.setUser(userRepository.findById(userId.value()).orElseThrow(UserNotFoundException::new));
        activityDbEntityRepository.save(activityDbEntry);
        return activityMapper.toActivityDetails(activityDbEntry);
    }

    @Override
    public void deleteForUser(UserId userId, ActivityUlid activityUlid) {
        ActivityDbEntity activityDbEntity = activityDbEntityRepository
            .findByUserIdAndUlid(userId.value(), activityUlid.value())
            .orElseThrow(ActivityNotFoundException::new);
        activityDbEntityRepository.delete(activityDbEntity);
    }
}
