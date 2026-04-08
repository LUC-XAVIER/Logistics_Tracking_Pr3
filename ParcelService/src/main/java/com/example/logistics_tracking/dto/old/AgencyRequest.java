package com.example.logistics_tracking.dto.old;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgencyRequest(
        @NotBlank String name,
        @NotBlank String country,
        @NotBlank String town,
        @NotBlank String addressLine,
        @NotNull Double latitude,
        @NotNull Double longitude
) {
}
