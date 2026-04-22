package com.example.paymentservice.service;

import com.example.paymentservice.event.PaymentCompletedEvent;
import com.example.paymentservice.event.PaymentFailedEvent;
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

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        try {
            kafkaTemplate.send(PARCEL_EVENTS_TOPIC, event.getParcelId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Published PaymentCompleted: parcelId={}, paymentId={}",
                                    event.getParcelId(), event.getPaymentId());
                        } else {
                            log.error("Failed to publish PaymentCompleted: parcelId={}",
                                    event.getParcelId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing PaymentCompleted event", e);
        }
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        try {
            kafkaTemplate.send(PARCEL_EVENTS_TOPIC, event.getParcelId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Published PaymentFailed: parcelId={}, paymentId={}",
                                    event.getParcelId(), event.getPaymentId());
                        } else {
                            log.error("Failed to publish PaymentFailed: parcelId={}",
                                    event.getParcelId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing PaymentFailed event", e);
        }
    }
}