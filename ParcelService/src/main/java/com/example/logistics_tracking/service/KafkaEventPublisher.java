package com.example.logistics_tracking.service;

import com.example.logistics_tracking.event.ParcelCreatedEvent;
import com.example.logistics_tracking.exception.KafkaPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PARCEL_EVENTS_TOPIC = "parcel-events";

    public void publishParcelCreated(ParcelCreatedEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(PARCEL_EVENTS_TOPIC, event.getParcelId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Published ParcelCreated event: parcelId={}, offset={}",
                            event.getParcelId(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish ParcelCreated event: parcelId={}",
                            event.getParcelId(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error publishing ParcelCreated event", e);
            throw KafkaPublishException.brokerUnavailable(e);
        }
    }
}