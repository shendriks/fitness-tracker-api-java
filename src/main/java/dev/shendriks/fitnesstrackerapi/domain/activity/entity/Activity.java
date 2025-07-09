package dev.shendriks.fitnesstrackerapi.domain.activity.entity;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityState;
import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.supportive.ulid.Ulid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;
    @Column(nullable = false)
    private int duration;
    @Column(nullable = false)
    private int calories;
    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
    @OneToMany(mappedBy = "activity", orphanRemoval = true)
    private List<GPSPosition> gpsPositions = List.of();
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityState state = ActivityState.STARTED;
}
