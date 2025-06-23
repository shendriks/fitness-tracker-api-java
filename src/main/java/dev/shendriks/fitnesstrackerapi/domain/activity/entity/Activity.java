package dev.shendriks.fitnesstrackerapi.domain.activity.entity;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
import dev.shendriks.fitnesstrackerapi.supportive.ulid.Ulid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter 
@NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue
    private long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @Column(nullable = false)
    // note: user != developer!
    private String username;
    @Column(nullable = false)
    private ActivityType activityType;
    @Column(nullable = false)
    private int duration;
    @Column(nullable = false)
    private int calories;
    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;
}
