package com.example.deliveryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripRequest {

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotNull(message = "Source agency ID is required")
    private UUID sourceAgencyId;

    @NotNull(message = "Destination agency ID is required")
    private UUID destAgencyId;
}