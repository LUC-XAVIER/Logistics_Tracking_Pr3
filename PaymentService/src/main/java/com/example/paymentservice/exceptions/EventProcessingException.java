package com.example.paymentservice.exceptions;

import lombok.Getter;

/**
 * Event Processing Exception
 *
 * Purpose: Thrown when consuming/processing Kafka events fails
 *
 * Scenarios:
 * - Invalid event format
 * - Missing required fields
 * - Business logic failure during event processing
 *
 * Does NOT return HTTP response (async processing)
 * Logged and optionally sent to dead letter queue
 */
@Getter
public class EventProcessingException extends RuntimeException {

    private final Object failedEvent;  // Store event for DLQ

    public EventProcessingException(String message, Object event, Throwable cause) {
        super(message, cause);
        this.failedEvent = event;
    }

    public static EventProcessingException invalidEventFormat(Object event) {
        return new EventProcessingException(
                "Event format is invalid or missing required fields",
                event,
                null
        );
    }

    public static EventProcessingException processingFailed(Object event, Throwable cause) {
        return new EventProcessingException(
                "Failed to process event: " + cause.getMessage(),
                event,
                cause
        );
    }
}