package com.example.logistics_tracking.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelRequest {

    @NotBlank(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Source agency ID is required")
    private String sourceAgencyId;

    @NotBlank(message = "Destination agency ID is required")
    private String destAgencyId;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    @DecimalMax(value = "100.0", message = "Weight cannot exceed 100 kg")
    private Double weight;

    @NotNull(message = "Fragility level is required")
    @Min(value = 1, message = "Fragility must be at least 1")
    @Max(value = 10, message = "Fragility cannot exceed 10")
    private Integer fragility;
}