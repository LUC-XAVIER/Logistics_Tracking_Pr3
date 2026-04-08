package com.example.paymentservice.exceptions;

import lombok.Getter;

/**
 * Business Exception
 *
 * Purpose: Thrown when business rules are violated
 *
 * Examples:
 * - Invalid parcel weight
 * - User not eligible for service
 * - Payment amount incorrect
 *
 * These are EXPECTED errors with user-friendly messages
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;  // Machine-readable code

    public BusinessException(String message, String errorCode) {
        super(message);  // User-friendly message
        this.errorCode = errorCode;
    }

    // Common business exceptions as static factory methods

    public static BusinessException invalidWeight() {
        return new BusinessException(
                "Parcel weight must be between 0.1 and 100 kg",
                "INVALID_WEIGHT"
        );
    }

    public static BusinessException invalidFragility() {
        return new BusinessException(
                "Fragility level must be between 1 and 10",
                "INVALID_FRAGILITY"
        );
    }

    public static BusinessException userNotFound(String userId) {
        return new BusinessException(
                "User with ID " + userId + " not found",
                "USER_NOT_FOUND"
        );
    }

    public static BusinessException agencyNotFound(String agencyId) {
        return new BusinessException(
                "Agency with ID " + agencyId + " not found or inactive",
                "AGENCY_NOT_FOUND"
        );
    }
}