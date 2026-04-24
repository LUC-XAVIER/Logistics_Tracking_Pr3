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
public class ParcelEventConsumer {

    private final NotificationDispatchService dispatchService;

    @KafkaListener(topics = "parcel-events", groupId = "notification-service-group")
    public void onParcelEvent(Object payload) {
        try {
            if (!(payload instanceof Map)) return;

            @SuppressWarnings("unchecked")
            Map<String, Object> event = (Map<String, Object>) payload;
            String eventType = (String) event.get("eventType");
            if (eventType == null) return;

            switch (eventType) {
                case "ParcelCreated" -> handleParcelCreated(event);
                default -> log.debug("ParcelEventConsumer ignored eventType={}", eventType);
            }

        } catch (Exception e) {
            log.error("Error processing parcel event: {}", e.getMessage(), e);
        }
    }

    private void handleParcelCreated(Map<String, Object> event) {
        String parcelId = (String) event.get("parcelId");
        UUID userId = extractUUID(event, "userId");
        if (userId == null) {
            log.warn("ParcelCreated event missing userId, parcelId={}", parcelId);
            return;
        }
        dispatchService.dispatch(
                userId,
                parcelId != null ? UUID.fromString(parcelId) : null,
                "Parcel Registered",
                "Your parcel #" + parcelId + " has been registered successfully.",
                NotificationEventType.PARCEL_CREATED
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