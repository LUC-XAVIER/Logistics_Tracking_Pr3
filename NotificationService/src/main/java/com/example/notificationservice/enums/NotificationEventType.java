package com.example.notificationservice.enums;

public enum NotificationEventType {
    // Parcel
    PARCEL_CREATED,
    PARCEL_CANCELLED,
    PARCEL_PICKUP_CONFIRMED,

    // Payment
    PAYMENT_INITIATED,
    PAYMENT_SUCCESSFUL,
    PAYMENT_FAILED,
    PAYMENT_REFUNDED,

    // Delivery
    DELIVERY_STARTED,
    DELIVERY_PROGRESS,
    DELIVERY_COMPLETED,
    DELIVERY_FAILED,
    DELIVERY_RESCHEDULED,

    // Legacy (keep for compatibility)
    PARCEL_LOCATION_UPDATED,
    PARCEL_DELIVERED
}