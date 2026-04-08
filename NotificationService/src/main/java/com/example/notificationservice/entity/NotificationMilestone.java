package com.example.notificationservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "notification_milestones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"parcelId", "milestone"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID parcelId;

    @Column(nullable = false)
    private Integer milestone; // 25, 50, 75

    private Instant notifiedAt;

    @PrePersist
    void onCreate() {
        notifiedAt = Instant.now();
    }
}