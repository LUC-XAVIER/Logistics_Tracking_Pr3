package com.example.logistics_tracking.entity;

import com.example.logistics_tracking.enums.ParcelStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Parcel Entity - Database table representation
 *
 * Purpose: Stores parcel information in database
 *
 * Maps to table: parcels
 *
 * Columns:
 * - id: Unique identifier (UUID)
 * - userId: Customer who sent parcel
 * - sourceAgencyId/destAgencyId: Pickup and delivery agencies
 * - sourceCoords/destCoords: GPS coordinates
 * - weight/fragility: Parcel attributes
 * - status: Current delivery status
 * - estimatedCost: Calculated cost
 * - estimatedDeliveryTime: When parcel should arrive
 * - createdAt/updatedAt: Audit timestamps
 */
@Entity  // JPA annotation - marks this as database table
@Table(name = "parcels")  // Table name in database
@Data  // Lombok - generates getters, setters, toString, equals, hashCode
@Builder  // Lombok - enables builder pattern: Parcel.builder().weight(5.5).build()
@NoArgsConstructor  // Lombok - generates no-args constructor (required by JPA)
@AllArgsConstructor  // Lombok - generates all-args constructor (used by builder)
public class Parcel {

    /**
     * Primary key - auto-generated UUID
     * Example: "550e8400-e29b-41d4-a716-446655440000"
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    /**
     * Customer ID who owns this parcel
     * Links to User Service (via Feign later)
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * Source agency ID (pickup location)
     * Links to Agency Service (via Feign later)
     */
    @Column(name = "source_agency_id", nullable = false)
    private String sourceAgencyId;

    /**
     * Destination agency ID (delivery location)
     * Links to Agency Service (via Feign later)
     */
    @Column(name = "dest_agency_id", nullable = false)
    private String destAgencyId;

    /**
     * Source coordinates (latitude)
     * Example: 3.8480 (Yaoundé)
     */
    @Column(name = "source_latitude", nullable = false)
    private Double sourceLatitude;

    /**
     * Source coordinates (longitude)
     * Example: 11.5021 (Yaoundé)
     */
    @Column(name = "source_longitude", nullable = false)
    private Double sourceLongitude;

    /**
     * Destination coordinates (latitude)
     */
    @Column(name = "dest_latitude", nullable = false)
    private Double destLatitude;

    /**
     * Destination coordinates (longitude)
     */
    @Column(name = "dest_longitude", nullable = false)
    private Double destLongitude;

    /**
     * Parcel weight in kilograms
     * Example: 5.5 kg
     *
     * Validation: 0.1 - 100 kg
     */
    @Column(name = "weight", nullable = false)
    private Double weight;

    /**
     * Fragility level (1-10)
     * 1 = Not fragile (books, clothes)
     * 10 = Extremely fragile (glass, electronics)
     *
     * Affects:
     * - Delivery speed (higher = slower)
     * - Cost (higher = more expensive)
     */
    @Column(name = "fragility", nullable = false)
    private Integer fragility;

    /**
     * Current delivery status
     * PENDING_PAYMENT → IN_TRANSIT → DELIVERED
     *
     * Stored as string in database ("PENDING_PAYMENT")
     */
    @Enumerated(EnumType.STRING)  // Store enum name, not ordinal
    @Column(name = "status", nullable = false)
    private ParcelStatus status;

    /**
     * Calculated delivery cost in XAF (Central African Franc)
     * Based on: distance, weight, fragility
     *
     * Example: 15000.00 XAF
     */
    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    /**
     * Estimated delivery time
     * Calculated from: distance, road type, fragility
     *
     * Example: "2026-04-07T14:00:00"
     */
    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    /**
     * When parcel was created (auto-set on insert)
     * Never updated after creation
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * When parcel was last modified (auto-updated)
     * Updates every time entity is saved
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA lifecycle callback - sets timestamps before persisting
     * Called automatically by JPA before INSERT
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Set default status if not provided
        if (status == null) {
            status = ParcelStatus.PENDING_PAYMENT;
        }
    }

    /**
     * JPA lifecycle callback - updates timestamp before updating
     * Called automatically by JPA before UPDATE
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}