package com.example.deliveryservice.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public static BusinessException tripNotFound(UUID tripId) {
        return new BusinessException(
                "Trip with ID " + tripId + " not found",
                "TRIP_NOT_FOUND"
        );
    }

    public static BusinessException segmentNotFound(UUID segmentId) {
        return new BusinessException(
                "Segment with ID " + segmentId + " not found",
                "SEGMENT_NOT_FOUND"
        );
    }

    public static BusinessException tripNotInCorrectStatus(String expected) {
        return new BusinessException(
                "Trip must be in " + expected + " status to perform this action",
                "INVALID_TRIP_STATUS"
        );
    }

    public static BusinessException parcelAlreadyAssigned(String parcelId) {
        return new BusinessException(
                "Parcel " + parcelId + " is already assigned to a trip",
                "PARCEL_ALREADY_ASSIGNED"
        );
    }

    public static BusinessException parcelNotAvailable(String parcelId) {
        return new BusinessException(
                "Parcel " + parcelId + " is not available for pickup",
                "PARCEL_NOT_AVAILABLE"
        );
    }

    public static BusinessException segmentAlreadyReached(UUID segmentId) {
        return new BusinessException(
                "Segment " + segmentId + " has already been marked as reached",
                "SEGMENT_ALREADY_REACHED"
        );
    }

    public static BusinessException osrmRouteFailed() {
        return new BusinessException(
                "Could not calculate route between the selected agencies",
                "ROUTE_CALCULATION_FAILED"
        );
    }

    public static BusinessException agencyNotFound(UUID agencyId) {
        return new BusinessException(
                "Agency with ID " + agencyId + " not found",
                "AGENCY_NOT_FOUND"
        );
    }
}