package com.example.logistics_tracking.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

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

    public static BusinessException invalidAddressInput() {
        return new BusinessException(
                "Provide either agency ID or manual address, not both",
                "INVALID_ADDRESS_INPUT"
        );
    }

    public static BusinessException missingAddressInput() {
        return new BusinessException(
                "Either agency ID or manual address is required",
                "MISSING_ADDRESS_INPUT"
        );
    }

    public static BusinessException agencyNotFound(String agencyId) {
        return new BusinessException(
                "Agency with ID " + agencyId + " not found",
                "AGENCY_NOT_FOUND"
        );
    }

    public static BusinessException parcelNotFound(String parcelId) {
        return new BusinessException(
                "Parcel with ID " + parcelId + " not found",
                "PARCEL_NOT_FOUND"
        );
    }

    public static BusinessException geocodingFailed(String address) {
        return new BusinessException(
                "Could not find coordinates for address: " + address,
                "GEOCODING_FAILED"
        );
    }

    public static BusinessException invalidStatus(String message) {
        return new BusinessException(message, "INVALID_STATUS");
    }
}
