package com.example.paymentservice.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public static BusinessException paymentNotFound(UUID paymentId) {
        return new BusinessException(
                "Payment with ID " + paymentId + " not found",
                "PAYMENT_NOT_FOUND"
        );
    }

    public static BusinessException paymentNotFoundForParcel(String parcelId) {
        return new BusinessException(
                "No payment found for parcel " + parcelId,
                "PAYMENT_NOT_FOUND"
        );
    }

    public static BusinessException paymentAlreadyCompleted(UUID paymentId) {
        return new BusinessException(
                "Payment " + paymentId + " has already been completed",
                "PAYMENT_ALREADY_COMPLETED"
        );
    }

    public static BusinessException paymentNotRetryable(UUID paymentId) {
        return new BusinessException(
                "Payment " + paymentId + " cannot be retried — only failed payments can be retried",
                "PAYMENT_NOT_RETRYABLE"
        );
    }

    public static BusinessException parcelAlreadyPaid(String parcelId) {
        return new BusinessException(
                "Parcel " + parcelId + " has already been paid for",
                "PARCEL_ALREADY_PAID"
        );
    }
}