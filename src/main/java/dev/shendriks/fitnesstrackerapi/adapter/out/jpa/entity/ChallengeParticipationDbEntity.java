package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import dev.shendriks.fitnesstrackerapi.infrastructure.supportive.ulid.Ulid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "challenge_participation", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "challenge_id"}))
public class ChallengeParticipationDbEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDbEntity user;
    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private ChallengeDbEntity challenge;
    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
    @Column(nullable = false)
    private int percentageCompleted;
}
