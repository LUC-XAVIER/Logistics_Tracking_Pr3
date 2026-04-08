package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.ParcelEventPayload;
import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParcelEventConsumer {

    private final NotificationDispatchService dispatchService;

    @KafkaListener(topics = "parcel.created", groupId = "notification-service-group")
    public void onParcelCreated(ParcelEventPayload payload) {
        log.info("Received parcel.created event for parcelId={}", payload.getParcelId());
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Parcel Registered",
                "Your parcel #" + payload.getParcelId() + " has been registered successfully.",
                NotificationEventType.PARCEL_CREATED,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }

    @KafkaListener(topics = "parcel.cancelled", groupId = "notification-service-group")
    public void onParcelCancelled(ParcelEventPayload payload) {
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Parcel Cancelled",
                "Your parcel #" + payload.getParcelId() + " has been cancelled.",
                NotificationEventType.PARCEL_CANCELLED,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }

    @KafkaListener(topics = "parcel.pickup.confirmed", groupId = "notification-service-group")
    public void onPickupConfirmed(ParcelEventPayload payload) {
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Parcel Picked Up",
                "Your parcel has been picked up by the delivery agent.",
                NotificationEventType.PARCEL_PICKUP_CONFIRMED,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }
}