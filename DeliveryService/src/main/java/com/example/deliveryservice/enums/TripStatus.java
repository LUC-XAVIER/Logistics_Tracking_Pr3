package com.example.deliveryservice.enums;

public enum TripStatus {
    COLLECTING,
    ACTIVE,
    COMPLETED;

    public boolean canAcceptParcels() {
        return this == COLLECTING;
    }

    public boolean canStart() {
        return this == COLLECTING;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}