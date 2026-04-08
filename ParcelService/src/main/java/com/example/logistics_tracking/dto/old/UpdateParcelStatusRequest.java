package com.example.logistics_tracking.dto.old;

import com.example.logistics_tracking.enums.ParcelStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateParcelStatusRequest(
        @NotNull ParcelStatus status,
        UUID paymentId,
        UUID deliveryId
) {
}
