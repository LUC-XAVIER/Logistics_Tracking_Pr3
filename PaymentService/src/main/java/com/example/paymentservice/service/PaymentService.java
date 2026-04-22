package com.example.paymentservice.service;

import com.example.paymentservice.dto.PayRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.event.PaymentCompletedEvent;
import com.example.paymentservice.event.PaymentFailedEvent;
import com.example.paymentservice.exception.BusinessException;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public Payment createPendingPayment(String parcelId, UUID userId, BigDecimal amount) {
        if (paymentRepository.existsByParcelIdAndStatus(parcelId, PaymentStatus.COMPLETED)) {
            throw BusinessException.parcelAlreadyPaid(parcelId);
        }

        Payment payment = Payment.builder()
                .parcelId(parcelId)
                .userId(userId)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Pending payment created: paymentId={}, parcelId={}, amount={}",
                saved.getId(), parcelId, amount);
        return saved;
    }

    @Transactional
    public PaymentResponse pay(UUID paymentId, PayRequest request) {
        Payment payment = findById(paymentId);

        if (payment.getStatus().isCompleted()) {
            throw BusinessException.paymentAlreadyCompleted(paymentId);
        }

        if (!payment.getStatus().isPending() && !payment.getStatus().canRetry()) {
            throw BusinessException.paymentNotRetryable(paymentId);
        }

        try {
            // Simulated payment — replace this block with real provider integration
            String transactionId = simulatePayment(payment.getAmount(), request);

            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setTransactionId(transactionId);
            payment.setFailureReason(null);
            payment.setPaidAt(Instant.now());
            paymentRepository.save(payment);

            publishCompleted(payment);
            log.info("Payment completed: paymentId={}, parcelId={}", paymentId, payment.getParcelId());

        } catch (PaymentSimulationException e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);

            publishFailed(payment, e.getMessage());
            log.warn("Payment failed: paymentId={}, reason={}", paymentId, e.getMessage());
        }

        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID paymentId) {
        return mapToResponse(findById(paymentId));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByParcelId(String parcelId) {
        Payment payment = paymentRepository.findByParcelId(parcelId)
                .orElseThrow(() -> BusinessException.paymentNotFoundForParcel(parcelId));
        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUserId(UUID userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private String simulatePayment(BigDecimal amount, PayRequest request) {
        // Simulated payment logic
        // Replace this method body with real provider SDK call when ready
        // e.g. MTN MoMo API, Campay, Orange Money API
        log.debug("Simulating {} payment for amount={}", request.getPaymentMethod(), amount);
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void publishCompleted(Payment payment) {
        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PaymentCompleted")
                .timestamp(Instant.now())
                .paymentId(payment.getId())
                .parcelId(payment.getParcelId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .build();
        kafkaEventPublisher.publishPaymentCompleted(event);
    }

    private void publishFailed(Payment payment, String reason) {
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PaymentFailed")
                .timestamp(Instant.now())
                .paymentId(payment.getId())
                .parcelId(payment.getParcelId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .failureReason(reason)
                .build();
        kafkaEventPublisher.publishPaymentFailed(event);
    }

    private Payment findById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> BusinessException.paymentNotFound(paymentId));
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .parcelId(payment.getParcelId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .failureReason(payment.getFailureReason())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    static class PaymentSimulationException extends RuntimeException {
        public PaymentSimulationException(String message) {
            super(message);
        }
    }
}