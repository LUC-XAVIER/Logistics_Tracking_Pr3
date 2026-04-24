package com.example.logistics_tracking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParcelQuoteRequest {
    @NotNull(message = "Source agency ID is required")
    private UUID sourceAgencyId;

    @NotNull(message = "Destination agency ID is required")
    private UUID destAgencyId;

    @Min(value = 0, message = "Weight must be non-negative")
    private double weight;

    @Min(value = 1, message = "Fragility must be at least 1")
    private int fragility;
}
