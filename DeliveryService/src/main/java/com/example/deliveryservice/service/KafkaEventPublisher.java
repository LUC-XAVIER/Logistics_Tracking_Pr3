package com.example.deliveryservice.service;

import com.example.deliveryservice.event.SegmentReachedEvent;
import com.example.deliveryservice.event.TripCompletedEvent;
import com.example.deliveryservice.event.TripStartedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PARCEL_EVENTS_TOPIC = "parcel-events";

    public void publishSegmentReached(SegmentReachedEvent event) {
        try {
            kafkaTemplate.send(PARCEL_EVENTS_TOPIC, event.getTripId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Published SegmentReached: tripId={}, segment={}/{}",
                                    event.getTripId(), event.getSegmentOrder(), event.getTotalSegments());
                        } else {
                            log.error("Failed to publish SegmentReached: tripId={}", event.getTripId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing SegmentReached event", e);
        }
    }

    public void publishTripCompleted(TripCompletedEvent event) {
        try {
            kafkaTemplate.send(PARCEL_EVENTS_TOPIC, event.getTripId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Published TripCompleted: tripId={}", event.getTripId());
                        } else {
                            log.error("Failed to publish TripCompleted: tripId={}", event.getTripId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing TripCompleted event", e);
        }
    }

    public void publishTripStarted(TripStartedEvent event) {
        try {
            kafkaTemplate.send(PARCEL_EVENTS_TOPIC, event.getTripId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Published TripStarted: tripId={}", event.getTripId());
                        } else {
                            log.error("Failed to publish TripStarted: tripId={}", event.getTripId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing TripStarted event", e);
        }
    }
}