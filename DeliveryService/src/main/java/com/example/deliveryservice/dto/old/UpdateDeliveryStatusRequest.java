package com.example.deliveryservice.dto.old;

import com.example.deliveryservice.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateDeliveryStatusRequest(
        @NotNull DeliveryStatus status
) {
}
