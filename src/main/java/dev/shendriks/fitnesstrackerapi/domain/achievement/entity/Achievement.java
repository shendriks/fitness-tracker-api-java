package dev.shendriks.fitnesstrackerapi.domain.achievement.entity;

import dev.shendriks.fitnesstrackerapi.domain.activity.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.supportive.ulid.Ulid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "challenge_id"}))
public class Achievement {
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne
    @JoinColumn(name = "challenge_id")
    Challenge challenge;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @OneToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;
}
