package com.example.paymentservice.enums;

public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED;

    public boolean canRetry() {
        return this == FAILED;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}