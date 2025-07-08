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
    private double latitude;
    @Column(nullable = false)
    private double longitude;
    @Column()
    private double altitude;
    @Column(nullable = false)
    private double accuracy;
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;
}
