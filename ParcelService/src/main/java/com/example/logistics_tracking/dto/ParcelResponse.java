package com.example.logistics_tracking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.logistics_tracking.enums.ParcelStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Parcel Response DTO
 *
 * Purpose: Returns parcel data to API consumers
 *
 * Used by: GET /api/parcels/{id}, POST /api/parcels
 *
 * Example JSON response:
 * {
 *   "id": "550e8400-e29b-41d4-a716-446655440000",
 *   "userId": "USER-123",
 *   "sourceAgencyId": "AG-001",
 *   "destAgencyId": "AG-050",
 *   "weight": 5.5,
 *   "fragility": 7,
 *   "status": "PENDING_PAYMENT",
 *   "estimatedCost": 15000.00,
 *   "estimatedDeliveryTime": "2026-04-07T14:00:00",
 *   "createdAt": "2026-04-06T10:00:00"
 * }
 *
 * Why separate from Entity:
 * - Hides internal fields (updatedAt)
 * - Controls JSON format (date formatting)
 * - Can add computed fields
 * - Cleaner API response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelResponse {

    /**
     * Unique parcel identifier
     * Auto-generated UUID
     */
    private String id;

    /**
     * User who owns this parcel
     */
    private String userId;

    /**
     * Source agency ID (pickup)
     */
    private String sourceAgencyId;

    /**
     * Destination agency ID (delivery)
     */
    private String destAgencyId;

    /**
     * Source coordinates (for map display)
     */
    private Double sourceLatitude;
    private Double sourceLongitude;

    /**
     * Destination coordinates (for map display)
     */
    private Double destLatitude;
    private Double destLongitude;

    /**
     * Parcel weight (kg)
     */
    private Double weight;

    /**
     * Fragility level (1-10)
     */
    private Integer fragility;

    /**
     * Current delivery status
     *
     * Frontend uses this to:
     * - Show status badge
     * - Enable/disable actions
     * - Display appropriate message
     */
    private ParcelStatus status;

    /**
     * Estimated delivery cost (XAF)
     *
     * Frontend displays: "15,000 XAF"
     */
    private BigDecimal estimatedCost;

    /**
     * Estimated delivery time
     *
     * Format: ISO-8601 (2026-04-07T14:00:00)
     * Frontend displays: "Apr 7, 2026 2:00 PM"
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;

    /**
     * When parcel was created
     *
     * Format: ISO-8601
     * Frontend displays: "Created: Apr 6, 2026"
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}