package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter.SpeedConverter;
import dev.shendriks.fitnesstrackerapi.domain.value.Speed;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "gpsposition")
public class GPSPositionDbEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Instant timestamp;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    @Column
    private Double altitude;
    @Column
    @Convert(converter = SpeedConverter.class)
    private Speed speed;
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private ActivityDbEntity activity;
}
