package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.PaymentEventPayload;
import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final NotificationDispatchService dispatchService;

    @KafkaListener(topics = "payment.initiated", groupId = "notification-service-group")
    public void onPaymentInitiated(PaymentEventPayload payload) {
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Payment Processing",
                "Your payment of " + payload.getAmount() + " " + payload.getCurrency() + " is being processed.",
                NotificationEventType.PAYMENT_INITIATED,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }

    @KafkaListener(topics = "payment.successful", groupId = "notification-service-group")
    public void onPaymentSuccessful(PaymentEventPayload payload) {
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Payment Confirmed",
                "Payment confirmed. Your parcel delivery will begin shortly.",
                NotificationEventType.PAYMENT_SUCCESSFUL,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-service-group")
    public void onPaymentFailed(PaymentEventPayload payload) {
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Payment Failed",
                "Your payment failed. Please retry to proceed with delivery.",
                NotificationEventType.PAYMENT_FAILED,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }

    @KafkaListener(topics = "payment.refunded", groupId = "notification-service-group")
    public void onPaymentRefunded(PaymentEventPayload payload) {
        dispatchService.dispatch(
                payload.getUserId(), payload.getParcelId(),
                "Refund Issued",
                "A refund of " + payload.getAmount() + " " + payload.getCurrency() + " has been issued for your parcel.",
                NotificationEventType.PAYMENT_REFUNDED,
                payload.getRecipientEmail(), payload.getRecipientPhone()
        );
    }
}