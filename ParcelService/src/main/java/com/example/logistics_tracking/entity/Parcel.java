package com.example.logistics_tracking.entity;

import com.example.logistics_tracking.enums.ParcelStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parcels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {

    @Id
    private UUID id;

    private UUID sourceAgencyId;

    private UUID destinationAgencyId;

    private String sourceAddress;

    private String destinationAddress;

    private Double sourceLatitude;

    private Double sourceLongitude;

    private Double destinationLatitude;

    private Double destinationLongitude;

    private BigDecimal weightKg;

    private Integer fragilityLevel;

    @Enumerated(EnumType.STRING)
    private ParcelStatus status;

    private Instant estimatedDeliveryTime;

    @Lob
    private String routeSegments;

    private UUID paymentId;

    private UUID deliveryId;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        Instant now = Instant.now();
        if (status == null) {
            status = ParcelStatus.PENDING;
        }
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
