package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter.DistanceConverter;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter.DurationConverter;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter.SpeedConverter;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityState;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import dev.shendriks.fitnesstrackerapi.domain.value.Speed;
import dev.shendriks.fitnesstrackerapi.infrastructure.supportive.ulid.Ulid;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activity")
public class ActivityDbEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserDbEntity user;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;
    @Convert(converter = DurationConverter.class)
    @Column(nullable = false)
    private Duration duration;
    @Convert(converter = DistanceConverter.class)
    @Column(nullable = false)
    private Distance distance;
    @Convert(converter = SpeedConverter.class)
    @Column(nullable = false)
    private Speed averageSpeed;
    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
    @Builder.Default
    @OneToMany(
        mappedBy = "activity",
        orphanRemoval = true,
        cascade = CascadeType.PERSIST,
        fetch = FetchType.LAZY
    )
    private List<GPSPositionDbEntity> gpsPositions = List.of();
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityState state = ActivityState.STARTED;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Instant startDate;
    @Convert(converter = DistanceConverter.class)
    @Column
    private Distance elevationGain;
    @Convert(converter = DurationConverter.class)
    @Column
    private Duration motionTime;
    @Convert(converter = DurationConverter.class)
    @Column
    private Duration pausingTime;
    @Builder.Default
    @OneToMany(
        mappedBy = "activity",
        orphanRemoval = true,
        cascade = CascadeType.PERSIST,
        fetch = FetchType.LAZY
    )
    private List<KilometerSpeedDbEntity> kilometerSpeeds = List.of();
}
