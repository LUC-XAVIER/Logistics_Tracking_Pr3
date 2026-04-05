package com.example.logistics_tracking.dto;

import java.util.UUID;

public record DeliveryBootstrapRequest(
        UUID parcelId
) {
}
