package com.example.deliveryservice.entity;

import com.example.deliveryservice.enums.TripStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes; 

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "driver_trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverTrip {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String driverId;

    @Column(nullable = false)
    private UUID sourceAgencyId;

    @Column(nullable = false)
    private UUID destAgencyId;

    @Column(nullable = false)
    private Double sourceLatitude;

    @Column(nullable = false)
    private Double sourceLongitude;

    @Column(nullable = false)
    private Double destLatitude;

    @Column(nullable = false)
    private Double destLongitude;

    @Column(nullable = false)
    private Double totalDistanceKm;

    @Column(nullable = false)
    private Integer segmentCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    @Builder.Default
    private List<double[]> fullPath = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TripSegment> segments = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TripParcel> parcels = new ArrayList<>();

    private Instant startedAt;
    private Instant completedAt;
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID();
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = TripStatus.COLLECTING;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}