package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.DeliveryEventPayload;
import com.example.notificationservice.entity.NotificationMilestone;
import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.repository.NotificationMilestoneRepository;
import com.example.notificationservice.service.NotificationDispatchService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventConsumer {

    private final NotificationDispatchService dispatchService;
    private final NotificationMilestoneRepository milestoneRepository;

    private static final Set<Integer> MILESTONES = Set.of(25, 50, 75);

    @KafkaListener(topics = "delivery.started", groupId = "notification-service-group")
    public void onDeliveryStarted(DeliveryEventPayload payload) {
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Delivery Started",
                "Your parcel is on its way!",
                NotificationEventType.DELIVERY_STARTED,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }

    @KafkaListener(topics = "delivery.progress", groupId = "notification-service-group")
    public void onDeliveryProgress(DeliveryEventPayload payload) {
        Integer progress = payload.getProgressPercent();
        if (progress == null || !MILESTONES.contains(progress)) return;

        boolean alreadyNotified = milestoneRepository
                .existsByParcelIdAndMilestone(payload.getParcelId(), progress);
        if (alreadyNotified) {
            log.info("Milestone {}% already notified for parcelId={}", progress, payload.getParcelId());
            return;
        }

        String body = switch (progress) {
            case 25 -> "Your parcel is 25% of the way to its destination.";
            case 50 -> "Your parcel is halfway there!";
            case 75 -> "Your parcel is almost at its destination.";
            default -> "Delivery in progress.";
        };

        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Delivery Update",
                body,
                NotificationEventType.DELIVERY_PROGRESS,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );

        milestoneRepository.save(NotificationMilestone.builder()
                .parcelId(payload.getParcelId())
                .milestone(progress)
                .build());
    }

    @KafkaListener(topics = "delivery.completed", groupId = "notification-service-group")
    public void onDeliveryCompleted(DeliveryEventPayload payload) {
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Parcel Delivered",
                "Your parcel has been delivered successfully. Thank you!",
                NotificationEventType.DELIVERY_COMPLETED,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }

    @KafkaListener(topics = "delivery.failed", groupId = "notification-service-group")
    public void onDeliveryFailed(DeliveryEventPayload payload) {
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Delivery Issue",
                "We encountered an issue delivering your parcel. Our team is on it.",
                NotificationEventType.DELIVERY_FAILED,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }

    @KafkaListener(topics = "delivery.rescheduled", groupId = "notification-service-group")
    public void onDeliveryRescheduled(DeliveryEventPayload payload) {
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Delivery Rescheduled",
                "Your delivery has been rescheduled. New ETA: " + payload.getNewEta(),
                NotificationEventType.DELIVERY_RESCHEDULED,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }
}