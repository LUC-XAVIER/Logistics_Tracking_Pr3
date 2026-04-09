package com.example.logistics_tracking.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    // Source: agency XOR manual address
    private String sourceAgencyId;
    private String sourceManualAddress;

    // Destination: agency XOR manual address
    private String destAgencyId;
    private String destManualAddress;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    @DecimalMax(value = "100.0", message = "Weight cannot exceed 100 kg")
    private Double weight;

    @NotNull(message = "Fragility level is required")
    @Min(value = 1, message = "Fragility must be at least 1")
    @Max(value = 10, message = "Fragility cannot exceed 10")
    private Integer fragility;

    public boolean hasSourceAgency() {
        return sourceAgencyId != null && !sourceAgencyId.isBlank();
    }

    public boolean hasSourceManualAddress() {
        return sourceManualAddress != null && !sourceManualAddress.isBlank();
    }

    public boolean hasDestAgency() {
        return destAgencyId != null && !destAgencyId.isBlank();
    }

    public boolean hasDestManualAddress() {
        return destManualAddress != null && !destManualAddress.isBlank();
    }
}