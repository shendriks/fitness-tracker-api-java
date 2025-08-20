package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "challenge")
public class ChallengeDbEntity extends AchievementDbEntity {
    @Column(nullable = false)
    private Instant startDate;
    @Column(nullable = false)
    private Instant endDate;
    @OneToMany(mappedBy = "challenge", fetch = FetchType.EAGER)
    private Set<ChallengeParticipationDbEntity> challengeParticipations;
}
