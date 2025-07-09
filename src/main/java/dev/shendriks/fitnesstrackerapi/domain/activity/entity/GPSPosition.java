package dev.shendriks.fitnesstrackerapi.domain.activity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GPSPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Instant timestamp;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    @Column()
    private Double altitude;
    @Column()
    private Double accuracy;
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;
}
