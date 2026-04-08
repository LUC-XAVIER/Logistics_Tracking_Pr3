package com.example.paymentservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 *
 * Purpose: Catches all exceptions in the application and converts them
 * to consistent JSON responses that frontend can easily handle
 *
 * Why needed:
 * - Frontend gets predictable error format
 * - Sensitive error details hidden from users
 * - Errors logged for debugging
 * - HTTP status codes set correctly
 */
@RestControllerAdvice  // Applies to all controllers in application
@Slf4j  // Enables logging
public class GlobalExceptionHandler {

    /**
     * Handles business logic errors (user-friendly messages)
     *
     * Example: Invalid parcel data, user not found, payment failed
     *
     * @param ex The business exception thrown
     * @return 400 BAD REQUEST with error details
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {}", ex.getMessage());  // Log for monitoring

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Business Error")
                .message(ex.getMessage())  // User-friendly message
                .code(ex.getErrorCode())  // Frontend can handle specific codes
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Kafka publishing errors
     *
     * Example: Kafka broker down, topic doesn't exist
     *
     * @param ex Kafka exception
     * @return 503 SERVICE UNAVAILABLE with error details
     */
    @ExceptionHandler(KafkaPublishException.class)
    public ResponseEntity<ErrorResponse> handleKafkaPublishException(KafkaPublishException ex) {
        log.error("Kafka publish failed: {}", ex.getMessage(), ex);  // Full stack trace in logs

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("Event Publishing Failed")
                .message("Unable to process request at this time. Please try again later.")  // Hide Kafka details from user
                .code("KAFKA_UNAVAILABLE")
                .build();

        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles event processing errors (consumer side)
     *
     * Example: Invalid event format, missing required fields
     *
     * @param ex Event processing exception
     * @return Logged only - doesn't return HTTP response (async processing)
     */
    @ExceptionHandler(EventProcessingException.class)
    public void handleEventProcessingException(EventProcessingException ex) {
        // Event processing is async - no HTTP response to send
        // Log error and send to dead letter queue for manual review

        log.error("Event processing failed: {}", ex.getMessage(), ex);

        // In production: Send failed event to dead letter queue
        // deadLetterQueueService.send(ex.getEvent(), ex.getMessage());
    }

    /**
     * Handles all other unexpected errors
     *
     * Example: NullPointerException, database connection errors
     *
     * @param ex Any uncaught exception
     * @return 500 INTERNAL SERVER ERROR with generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);  // Full details logged

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Our team has been notified.")  // Generic message for user
                .code("INTERNAL_ERROR")
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}