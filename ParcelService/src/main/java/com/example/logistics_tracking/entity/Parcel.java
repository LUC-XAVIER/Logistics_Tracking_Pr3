package com.example.logistics_tracking.entity;

import com.example.logistics_tracking.enums.ParcelStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "parcels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private UUID userId;

    // Source: Either agency OR manual address
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_agency_id")
    private Agency sourceAgency;

    @Column(nullable = false)
    private Double sourceLatitude;

    @Column(nullable = false)
    private Double sourceLongitude;

    // Destination: Either agency OR manual address
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dest_agency_id")
    private Agency destAgency;

    @Column(nullable = false)
    private Double destLatitude;

    @Column(nullable = false)
    private Double destLongitude;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Integer fragility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    private LocalDateTime estimatedDeliveryTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ParcelStatus.PENDING_PAYMENT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}