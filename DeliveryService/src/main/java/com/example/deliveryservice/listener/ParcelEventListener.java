package com.example.deliveryservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParcelEventListener {

    @KafkaListener(topics = "parcel-events", groupId = "delivery-service-group")
    public void handleParcelEvent(Object payload) {
        try {
            if (!(payload instanceof Map)) {
                log.warn("Received unexpected payload type: {}", payload.getClass());
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> event = (Map<String, Object>) payload;
            String eventType = (String) event.get("eventType");

            if (eventType == null) {
                log.warn("Received event with no eventType field");
                return;
            }

            switch (eventType) {
                case "PaymentCompleted" -> handlePaymentCompleted(event);
                default -> log.debug("Ignored event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Error processing parcel event: {}", e.getMessage(), e);
        }
    }

    private void handlePaymentCompleted(Map<String, Object> event) {
        String parcelId = (String) event.get("parcelId");
        log.info("PaymentCompleted received for parcelId={}. " +
                "Parcel status update to WAITING_FOR_DRIVER is handled by Parcel Service.", parcelId);
        // Delivery Service has no action on PaymentCompleted.
        // This listener is ready for future events that require delivery action.
    }
}