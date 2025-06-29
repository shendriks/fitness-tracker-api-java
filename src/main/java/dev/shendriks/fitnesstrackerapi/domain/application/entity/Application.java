package dev.shendriks.fitnesstrackerapi.domain.application.entity;

import dev.shendriks.fitnesstrackerapi.domain.user.enums.AccountType;
//import dev.shendriks.fitnesstrackerapi.domain.developer.entity.Developer;
import dev.shendriks.fitnesstrackerapi.supportive.ulid.Ulid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Ulid
    @Column(nullable = false, unique = true, length = 26)
    private String ulid;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, unique = true)
    private String apiKey;
//    @ManyToOne
//    @JoinColumn(name = "developer_id", nullable = false)
//    private Developer developer;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
