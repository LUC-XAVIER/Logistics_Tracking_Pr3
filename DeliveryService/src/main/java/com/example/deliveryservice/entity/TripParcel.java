package com.example.deliveryservice.entity;

import com.example.deliveryservice.enums.TripParcelStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trip_parcels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripParcel {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private DriverTrip trip;

    @Column(nullable = false)
    private String parcelId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripParcelStatus status;

    private Instant assignedAt;
    private Instant deliveredAt;
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID();
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = TripParcelStatus.ASSIGNED;
        if (assignedAt == null) assignedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}