package com.example.logistics_tracking.enums;

public enum ParcelStatus {
    PENDING_PAYMENT,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED,
    RETURNED,
    LOST;

    public boolean isFinalState() {
        return this == DELIVERED || this == CANCELLED || this == LOST;
    }

    public boolean canBeCancelled() {
        return this == PENDING_PAYMENT || this == IN_TRANSIT;
    }

    public boolean isActiveDelivery() {
        return this == IN_TRANSIT;
    }
}