package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "challenge")
public class ChallengeDbEntity extends AchievementDbEntity {
    @Column(nullable = false)
    private Instant startDate;
    @Column(nullable = false)
    private Instant endDate;
    @OneToMany(mappedBy = "challenge", fetch = FetchType.EAGER)
    private Set<ChallengeParticipationDbEntity> challengeParticipations;
}
