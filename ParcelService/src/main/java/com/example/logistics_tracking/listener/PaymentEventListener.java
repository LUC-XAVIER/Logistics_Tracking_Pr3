package com.example.logistics_tracking.listener;

import com.example.logistics_tracking.service.ParcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final ParcelService parcelService;

    @KafkaListener(topics = "parcel-events", groupId = "parcel-service-group")
    public void handlePaymentEvent(Object payload) {
        try {
            if (!(payload instanceof Map)) {
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> event = (Map<String, Object>) payload;
            String eventType = (String) event.get("eventType");

            if (eventType == null) return;

            switch (eventType) {
                case "PaymentCompleted" -> handlePaymentCompleted(event);
                case "TripStarted" -> handleTripStarted(event);
                case "TripCompleted" -> handleTripCompleted(event);
                default -> log.debug("ParcelService PaymentEventListener ignored eventType={}", eventType);
            }

        } catch (Exception e) {
            log.error("Error processing event in Parcel Service: {}", e.getMessage(), e);
        }
    }

    private void handlePaymentCompleted(Map<String, Object> event) {
        String parcelId = (String) event.get("parcelId");

        if (parcelId == null) {
            log.warn("PaymentCompleted event missing parcelId");
            return;
        }

        parcelService.updateParcelStatusToWaitingForAgent(parcelId);
        log.info("Handled PaymentCompleted event for parcelId={}", parcelId);
    }

    private void handleTripStarted(Map<String, Object> event) {
        @SuppressWarnings("unchecked")
        List<String> parcelIds = (List<String>) event.get("parcelIds");
        if (parcelIds != null && !parcelIds.isEmpty()) {
            parcelService.updateParcelsToInTransit(parcelIds);
            log.info("Handled TripStarted event for {} parcels", parcelIds.size());
        }
    }

    private void handleTripCompleted(Map<String, Object> event) {
        @SuppressWarnings("unchecked")
        List<String> parcelIds = (List<String>) event.get("parcelIds");
        if (parcelIds != null && !parcelIds.isEmpty()) {
            parcelService.updateParcelsToDelivered(parcelIds);
            log.info("Handled TripCompleted event for {} parcels", parcelIds.size());
        }
    }
}
