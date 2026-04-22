package com.example.notificationservice.consumer;

import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final NotificationDispatchService dispatchService;

    @KafkaListener(topics = "parcel-events", groupId = "notification-service-group")
    public void onPaymentEvent(Object payload) {
        try {
            if (!(payload instanceof Map)) return;

            @SuppressWarnings("unchecked")
            Map<String, Object> event = (Map<String, Object>) payload;
            String eventType = (String) event.get("eventType");
            if (eventType == null) return;

            switch (eventType) {
                case "PaymentCompleted" -> handlePaymentCompleted(event);
                default -> log.debug("PaymentEventConsumer ignored eventType={}", eventType);
            }

        } catch (Exception e) {
            log.error("Error processing payment event: {}", e.getMessage(), e);
        }
    }

    private void handlePaymentCompleted(Map<String, Object> event) {
        String parcelId = (String) event.get("parcelId");
        UUID userId = extractUUID(event, "userId");
        if (userId == null) {
            log.warn("PaymentCompleted event missing userId, parcelId={}", parcelId);
            return;
        }
        dispatchService.dispatch(
                userId,
                parcelId != null ? UUID.fromString(parcelId) : null,
                "Payment Confirmed",
                "Payment confirmed. Your parcel will be available for pickup shortly.",
                NotificationEventType.PAYMENT_SUCCESSFUL
        );
    }

    private UUID extractUUID(Map<String, Object> event, String key) {
        Object val = event.get(key);
        if (val == null) return null;
        if (val instanceof String s) {
            try { return UUID.fromString(s); } catch (IllegalArgumentException ignored) {}
        }
        return null;
    }
}