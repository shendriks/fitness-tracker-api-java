package dev.shendriks.fitnesstrackerapi.domain.achievement.entity;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.supportive.ulid.Ulid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 1023)
    private String description;
    @Column(nullable = false)
    private String imageFilePath;
    @Column
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityMetric activityMetric;
    @Column(nullable = false)
    private Long completionThreshold;
    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
