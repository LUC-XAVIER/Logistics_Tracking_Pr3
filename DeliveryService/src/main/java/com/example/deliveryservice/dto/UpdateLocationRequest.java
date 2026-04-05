package com.example.deliveryservice.dto;

import com.example.deliveryservice.enums.RoadType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateLocationRequest(
        @NotNull Double latitude,
        @NotNull Double longitude,
        @NotNull @Min(0) @Max(100) Integer progressPercentage,
        RoadType roadType,
        Integer sequenceNumber
) {
}
