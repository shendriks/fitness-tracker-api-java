package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.type.Ulid;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trophy", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "achievement_id"}))
public class TrophyDbEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDbEntity user;
    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private AchievementDbEntity achievement;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
