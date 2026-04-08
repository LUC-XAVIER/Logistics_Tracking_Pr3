package com.example.logistics_tracking.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Parcel Request DTO
 *
 * Purpose: Accepts parcel creation data from API
 *
 * Used by: POST /api/parcels endpoint
 *
 * Example JSON:
 * {
 *   "userId": "USER-123",
 *   "sourceAgencyId": "AG-001",
 *   "destAgencyId": "AG-050",
 *   "weight": 5.5,
 *   "fragility": 7
 * }
 *
 * Validation rules enforced:
 * - All fields required
 * - Weight: 0.1 - 100 kg
 * - Fragility: 1 - 10
 *
 * Why separate from Entity:
 * - Entity has auto-generated fields (id, timestamps)
 * - DTO only accepts user input
 * - Cleaner API contract
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelRequest {

    /**
     * User ID who owns this parcel
     *
     * Validation: Required, not blank
     * Example: "USER-12345"
     */
    @NotBlank(message = "User ID is required")
    private String userId;

    /**
     * Source agency ID (pickup location)
     *
     * Validation: Required, not blank
     * Example: "AG-001"
     */
    @NotBlank(message = "Source agency ID is required")
    private String sourceAgencyId;

    /**
     * Destination agency ID (delivery location)
     *
     * Validation: Required, not blank
     * Example: "AG-050"
     */
    @NotBlank(message = "Destination agency ID is required")
    private String destAgencyId;

    /**
     * Parcel weight in kilograms
     *
     * Validation:
     * - Required (not null)
     * - Minimum: 0.1 kg
     * - Maximum: 100 kg
     *
     * Example: 5.5
     */
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    @DecimalMax(value = "100.0", message = "Weight cannot exceed 100 kg")
    private Double weight;

    /**
     * Fragility level (1-10)
     *
     * Validation:
     * - Required (not null)
     * - Minimum: 1 (not fragile)
     * - Maximum: 10 (extremely fragile)
     *
     * Example: 7
     */
    @NotNull(message = "Fragility level is required")
    @Min(value = 1, message = "Fragility level must be at least 1")
    @Max(value = 10, message = "Fragility level cannot exceed 10")
    private Integer fragility;
}