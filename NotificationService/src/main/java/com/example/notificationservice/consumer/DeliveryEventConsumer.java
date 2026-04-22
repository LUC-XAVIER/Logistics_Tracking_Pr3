package com.example.notificationservice.consumer;

import com.example.notificationservice.client.ParcelOwnerClient;
import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventConsumer {

    private final NotificationDispatchService dispatchService;
    private final ParcelOwnerClient parcelOwnerClient;

    @KafkaListener(topics = "parcel-events", groupId = "notification-service-group")
    public void onDeliveryEvent(Object payload) {
        try {
            if (!(payload instanceof Map)) return;

            @SuppressWarnings("unchecked")
            Map<String, Object> event = (Map<String, Object>) payload;
            String eventType = (String) event.get("eventType");
            if (eventType == null) return;

            switch (eventType) {
                case "SegmentReached" -> handleSegmentReached(event);
                case "TripCompleted" -> handleTripCompleted(event);
                default -> log.debug("DeliveryEventConsumer ignored eventType={}", eventType);
            }

        } catch (Exception e) {
            log.error("Error processing delivery event: {}", e.getMessage(), e);
        }
    }

    private void handleSegmentReached(Map<String, Object> event) {
        Integer segmentOrder = (Integer) event.get("segmentOrder");
        Integer totalSegments = (Integer) event.get("totalSegments");
        Double distanceTraveled = extractDouble(event, "distanceTraveledKm");
        Double distanceRemaining = extractDouble(event, "distanceRemainingKm");

        @SuppressWarnings("unchecked")
        List<String> parcelIds = (List<String>) event.get("parcelIds");
        if (parcelIds == null || parcelIds.isEmpty()) return;

        String message = String.format(
                "Checkpoint %d/%d reached. Distance traveled: %.1f km, remaining: %.1f km.",
                segmentOrder, totalSegments, distanceTraveled, distanceRemaining
        );

        for (String parcelId : parcelIds) {
            notifyParcelOwner(parcelId, "Delivery Update", message,
                    NotificationEventType.DELIVERY_PROGRESS);
        }
    }

    private void handleTripCompleted(Map<String, Object> event) {
        @SuppressWarnings("unchecked")
        List<String> parcelIds = (List<String>) event.get("parcelIds");
        if (parcelIds == null || parcelIds.isEmpty()) return;

        for (String parcelId : parcelIds) {
            notifyParcelOwner(parcelId, "Parcel Delivered",
                    "Your parcel has been delivered successfully. Thank you!",
                    NotificationEventType.DELIVERY_COMPLETED);
        }
    }

    private void notifyParcelOwner(String parcelId, String title,
                                   String message, NotificationEventType eventType) {
        try {
            UUID userId = parcelOwnerClient.getParcelOwner(parcelId);
            if (userId == null) {
                log.warn("Could not resolve owner for parcelId={}", parcelId);
                return;
            }
            dispatchService.dispatch(userId, UUID.fromString(parcelId),
                    title, message, eventType);
        } catch (Exception e) {
            log.error("Failed to notify owner for parcelId={}: {}", parcelId, e.getMessage());
        }
    }

    private Double extractDouble(Map<String, Object> event, String key) {
        Object val = event.get(key);
        if (val == null) return 0.0;
        if (val instanceof Double d) return d;
        if (val instanceof Integer i) return i.doubleValue();
        if (val instanceof String s) {
            try { return Double.parseDouble(s); } catch (NumberFormatException ignored) {}
        }
        return 0.0;
    }
}