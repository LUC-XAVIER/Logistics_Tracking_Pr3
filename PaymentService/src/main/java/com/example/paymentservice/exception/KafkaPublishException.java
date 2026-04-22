package com.example.paymentservice.exception;

/**
 * Kafka Publish Exception
 *
 * Purpose: Thrown when event publishing to Kafka fails
 *
 * Scenarios:
 * - Kafka broker unreachable
 * - Topic doesn't exist
 * - Serialization error
 *
 * Indicates infrastructure issue, not user error
 */
public class KafkaPublishException extends RuntimeException {

    public KafkaPublishException(String message, Throwable cause) {
        super(message, cause);
    }

    public static KafkaPublishException brokerUnavailable(Throwable cause) {
        return new KafkaPublishException(
                "Kafka broker is currently unavailable",
                cause
        );
    }

    public static KafkaPublishException serializationFailed(Throwable cause) {
        return new KafkaPublishException(
                "Failed to serialize event for publishing",
                cause
        );
    }
}