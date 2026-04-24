package com.example.paymentservice.listener;

import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParcelEventListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = "parcel-events", groupId = "payment-service-group")
    public void handleParcelEvent(Object payload) {
        try {
            if (!(payload instanceof Map)) return;

            @SuppressWarnings("unchecked")
            Map<String, Object> event = (Map<String, Object>) payload;
            String eventType = (String) event.get("eventType");
            if (eventType == null) return;

            switch (eventType) {
                case "ParcelCreated" -> handleParcelCreated(event);
                default -> log.debug("PaymentService ignored eventType={}", eventType);
            }

        } catch (Exception e) {
            log.error("Error processing parcel event in Payment Service: {}", e.getMessage(), e);
        }
    }

    private void handleParcelCreated(Map<String, Object> event) {
        String parcelId = (String) event.get("parcelId");
        UUID userId = extractUUID(event, "userId");
        BigDecimal amount = extractBigDecimal(event, "estimatedCost");

        if (parcelId == null || userId == null || amount == null) {
            log.warn("ParcelCreated event missing required fields: parcelId={}, userId={}, amount={}",
                    parcelId, userId, amount);
            return;
        }

        paymentService.createPendingPayment(parcelId, userId, amount);
        log.info("Pending payment created from ParcelCreated event: parcelId={}", parcelId);
    }

    private UUID extractUUID(Map<String, Object> event, String key) {
        Object val = event.get(key);
        if (val == null) return null;
        if (val instanceof String s) {
            try { return UUID.fromString(s); } catch (IllegalArgumentException ignored) {}
        }
        return null;
    }

    private BigDecimal extractBigDecimal(Map<String, Object> event, String key) {
        Object val = event.get(key);
        if (val == null) return null;
        if (val instanceof BigDecimal bd) return bd;
        if (val instanceof Double d) return BigDecimal.valueOf(d);
        if (val instanceof Integer i) return BigDecimal.valueOf(i);
        if (val instanceof String s) {
            try { return new BigDecimal(s); } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}