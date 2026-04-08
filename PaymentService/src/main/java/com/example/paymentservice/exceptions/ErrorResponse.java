package com.example.paymentservice.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Error Response DTO
 *
 * Purpose: Standardized error format for all API responses
 *
 * Frontend receives consistent JSON structure:
 * {
 *   "timestamp": "2026-04-08T10:30:00",
 *   "status": 400,
 *   "error": "Business Error",
 *   "message": "Parcel weight must be between 0.1 and 100 kg",
 *   "code": "INVALID_WEIGHT"
 * }
 *
 * Benefits:
 * - Frontend can parse errors consistently
 * - Error codes enable specific handling (e.g., show different UI for different codes)
 * - Timestamp helps with debugging
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * When the error occurred
     * Format: ISO-8601 (2026-04-08T10:30:00)
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * HTTP status code (400, 404, 500, etc.)
     * Frontend can use this for general error categorization
     */
    private int status;

    /**
     * Error category (e.g., "Business Error", "Validation Error")
     * Human-readable error type
     */
    private String error;

    /**
     * User-friendly error message
     * Safe to display to end users
     */
    private String message;

    /**
     * Application-specific error code
     * Frontend can use for specific handling:
     *
     * Examples:
     * - "INVALID_WEIGHT" → Show weight validation message
     * - "USER_NOT_FOUND" → Redirect to login
     * - "KAFKA_UNAVAILABLE" → Show retry button
     */
    private String code;
}