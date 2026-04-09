package com.example.logistics_tracking.exception;

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
}