package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter.DistanceConverter;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter.DurationConverter;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityState;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
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
    //    @Type(value = DurationType.class)
    @Convert(converter = DurationConverter.class)
    @Column(nullable = false)
    private Duration duration;
    //    @Type(value = DistanceType.class)
    @Convert(converter = DistanceConverter.class)
    @Column(nullable = false)
    private Distance distance;
    @Column(nullable = false)
    private int calories;
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
}

